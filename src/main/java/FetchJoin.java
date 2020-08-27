import Jpql.Member;
import Jpql.MemberType;
import Jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class FetchJoin {
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

        member.changeTeam(team);
        member2.changeTeam(team);

        EM.persist(member);
        EM.persist(member2);
        EM.persist(team);

        EM.flush();
        EM.clear();

        String query = "select m from Member m join fetch m.team";
        String query2 = "select t from Team t join fetch t.members";
        List<Member> resultList = EM.createQuery(query, Member.class).getResultList();
        List<Team> resultList2 = EM.createQuery(query2, Team.class).getResultList();

        //페치조인으로 가져오면 지연로딩을 무시하고 즉시로딩으로 가져온다
        for (Member memberList : resultList) {
            System.out.println(memberList.getName());
        }

        //teamA를 두번 가져온다 왜냐하면 teamA Junseok  / teamA Junsoku 이렇게 가져오기때문 (1:다 의 경우)
        for (Team teamList : resultList2) {
            System.out.println(teamList.getName());
            System.out.println(teamList.getMembers().size());
        }

        //distinct로 중복을 제거해준다
        String query3 = "select distinct t from Team t join fetch t.members";
        List<Team> resultList3 = EM.createQuery(query3, Team.class).getResultList();
        for (Team teamList2 : resultList3) {
            System.out.println(teamList2.getMembers().size());
        }

        //fetch = EAGER일경우 그냥 조인해도 차이가 없어보인다.
        // 즉, fetch조인은 지연로딩으로 설정된 객체를 쿼리한줄로 즉시로딩으로 받아옴
        //++추가사항. 즉시로딩은 무한재귀현상이 일어날수있는데 페치조인은 그런게없고 로딩시간이 더 빠르다.
//        String query4 = "select distinct t from Team t join t.members";
//        List<Team> resultList4 = EM.createQuery(query4, Team.class).getResultList();
//        for (Team teamList3 : resultList4) {
//            System.out.println(teamList3.getMembers().size());
//        }

        TS.commit();
        EM.close();
        EMF.close();
    }
}
