import Jpql.Member;
import Jpql.MemberType;
import Jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class NamedQuery {

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

        //쿼리의 객체화 >> 컴파일할떄 객체화된 쿼리를 캐시에넣고 쿼리를 미리 검증
        //즉 작업속도, 로딩속도도 빨라질뿐더러, 버그도 컴파일시점에서 잡아준다.

        //XML에 정의한뒤 어노테이션으로 불러오는 방법도 있다.
        //스프링데이터JPA를 쓰면 DAO를 좀더 깔끔하게 관리할수있다.
        Member memberResult = EM.createNamedQuery("Member.findByName", Member.class)
                .setParameter("name", "Junseok").getSingleResult();
        System.out.println(memberResult.getName());

        TS.commit();
        EM.close();
        EMF.close();
    }
}
