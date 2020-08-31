import Jpql.Member;
import Jpql.MemberType;
import Jpql.Team;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class Bulk {

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
        member.setName("Junseok");
        member.setAge(29);
        member.setType(MemberType.USER);
        Member member3 = new Member();
        member.setName("Junseok");
        member.setAge(29);
        member.setType(MemberType.USER);

        EM.persist(member);
        EM.persist(member2);
        EM.persist(member3);

        //재고가 10개미만인 모든 상품의 가격을 10% 상승시키려면?
        //기존의방식 : 10개미만인 모든 상품의 리스트를 가져와서 setPrice로 다 바꿔준다음 
        // persist()를통해 변경감지 > 변경된 데이터의 갯수만큼 sql을 날림
        
        //벌크연산 : 쿼리한방에 모두 수정, 하지만 영속성컨텍스트를 무시하고 바로 쿼리를 날림
        int resultCount = EM.createQuery("update Member m set m.age = 30").executeUpdate();

        System.out.println(resultCount);

        //그로인해 발생되는 문제점 : DB는 업데이트가 되지만 영속성컨텍스트에는 최신화가 안됨
        // >> 그렇기때문에 먼저 벌크연산후 작업하거나, 벌크연산 수행 후 영속성컨텍스트 초기화

        Member findMember = EM.find(Member.class, member.getId());
        System.out.println(findMember.getAge());
        
        //초기화후에 정상적으로 값을 가져오는 모습
        EM.clear();

        Member findMember2 = EM.find(Member.class, member.getId());
        System.out.println(findMember2.getAge());
        TS.commit();
        EM.close();
        EMF.close();
    }
}
