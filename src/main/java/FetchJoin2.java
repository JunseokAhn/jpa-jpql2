import Jpql.Member;
import Jpql.MemberType;
import Jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FetchJoin2 {
    public static void main(String[] args) {
        EntityManagerFactory EMF = Persistence.createEntityManagerFactory("hello");
        EntityManager EM = EMF.createEntityManager();
        EntityTransaction TS = EM.getTransaction();

        TS.begin();

        Member member = new Member();
        member.setName("Junseok");
        member.setAge(29);
        member.setType(MemberType.USER);

        Member member2 = new Member();
        member2.setName("Junsoku");
        member2.setAge(29);
        member2.setType(MemberType.USER);


        Team team = new Team();
        team.setName("TeamA");

        Team team2 = new Team();
        team2.setName("TeamB");

        member.changeTeam(team);
        member2.changeTeam(team2);

        EM.persist(member);
        EM.persist(member2);
        EM.persist(team);
        EM.persist(team2);

        EM.flush();
        EM.clear();

        //페치조인에 별칭, 조건절을 줄 수 없다 오작동의 원인이됨
        //둘 이상의 컬렉션을 페치조인할 수 없다
        //컬렉션을 페치조인하면 페이징API를 사용할수없다

        String query = "select t from Team t";
        List<Team> resultList = EM.createQuery(query, Team.class).getResultList();

        //fetch=LAZY로 두었기때문에 teamList가 바뀔때마다 쿼리가 계속 나가는 문제점 발생
        //JPA의 대다수의 문제점인 N+1문제를 페치조인 + 배치사이즈로 해결가능(테이블 수만큼 쿼리나감)
        // where members.team_id in (?, ?) << 배치사이즈만큼 in이 늘어남

        for (Team teamList : resultList) {
            System.out.println(teamList.getName());
            System.out.println(teamList.getMembers().size());
            for (Member teamMember : team.getMembers()) {
                System.out.println(teamMember.getName());
            }
        }

        TS.commit();
        EM.close();
        EMF.close();
    }
}
