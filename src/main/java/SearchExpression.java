import Jpql.Item;
import Jpql.Member;
import Jpql.MemberType;
import Jpql.Team;
import org.hibernate.mapping.Collection;

import javax.persistence.*;
import java.util.List;

public class SearchExpression {

    public static void main(String[] args) {

        EntityManagerFactory EMF = Persistence.createEntityManagerFactory("hello");
        EntityManager EM = EMF.createEntityManager();
        EntityTransaction TS = EM.getTransaction();


        TS.begin();

        Member member = new Member();
        member.setName("Junseok");
        member.setAge(29);
        member.setType(MemberType.USER);

        Team team = new Team();
        team.setName("TEAMA");

        member.changeTeam(team);

        EM.persist(member);
        EM.persist(team);

        EM.flush();
        EM.clear();

        //단일 연관경로 쿼리 >> 묵시적 inner join발생 >> 쿼리 튜닝하기에 어렵다
        String query = "select m.team from Member m";

        //컬렉션 연관경로 쿼리 >> 묵시적 inner join 발생, 하지만 탐색은 불가즉 t.members.name 안됨
        String query2 = "select t.members from Team t";

        // 굳이하려면 t.members.size정도?
        String query3 = "select t.members.size from Team t";
        
        List<Integer> resultList3 = EM.createQuery(query3, Integer.class).getResultList();

        for (Integer integer : resultList3) {
            System.out.println(integer);
        }

        //즉, 명시적 조인 필요. 실무에선 명시적조인을 써야 함.
        String query4 = "select m.name, t.name from Member m join Team t on m.team = t.id";
        String query5 = "select m.name, t.name from Member m join m.team t";
        String query6 = "select m.name, t.name from Team t join t.members m";

        List<Object[]> resultList = EM.createQuery(query6).getResultList();
        for (Object[] objects : resultList) {
            for (Object object : objects) {
                System.out.println(object);
            }
        }

        //엔티티 자체를 파라미터로 넘기면 하이버네이트가 id값을 자동으로 할당해서 sql을 쏴준다
        String entityQuery = "select m from Member m where m = :member";
        Member entityResult = EM.createQuery(entityQuery, Member.class)
                .setParameter("member", member).getSingleResult();
        System.out.println(entityResult.getName());


        String entityQuery2 = "select m from Member m where m.id = :member";
        Member entityResult2 = EM.createQuery(entityQuery2, Member.class)
                .setParameter("member", member.getId()).getSingleResult();
        System.out.println(entityResult2.getName());


        //엔티티 직접할당
        String entityQuery3 = "select count(m) from Member m";
        Long entityResult3 = EM.createQuery(entityQuery3, Long.class).getSingleResult();
        System.out.println(entityResult3);


        String entityQuery4 = "select m from Member m where m.team = :team";
        Member entityResult4 = EM.createQuery(entityQuery4, Member.class)
                .setParameter("team", team).getSingleResult();
        System.out.println(entityResult4.getName());

        TS.commit();
        EM.close();
        EMF.close();
    }
}
