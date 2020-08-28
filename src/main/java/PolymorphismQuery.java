import Jpql.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.List;

public class PolymorphismQuery {
    public static void main(String[] args) {
        EntityManagerFactory EMF = Persistence.createEntityManagerFactory("hello");
        EntityManager EM = EMF.createEntityManager();
        EntityTransaction TS = EM.getTransaction();

        TS.begin();

        Book book = new Book();
        book.setAuthor("hive");
        book.setName("Junsoku");

        Movie movie = new Movie();
        movie.setDirector("rear");
        movie.setName("Junseok");

        EM.persist(book);
        EM.persist(movie);
        EM.flush();
        EM.clear();

        //Item을 전부 Book으로 바꾼다면 자식클래스인 book으로 가져올수도있음.
        String query = "select i from Item i where type(i) in (Book, Movie)";
        List<Item> resultList = EM.createQuery(query, Item.class).getResultList();
        for (Item itemList : resultList) {
            System.out.println(itemList.getName());
            System.out.println(itemList.getClass());
        }

        //treat를 이용해서 item의 자식인 book으로 다운캐스팅한다음 book의 author를 조건절로 달수있다.
        String query2 = "select i from Item i where treat(i as Book).author = 'hive'";
        List<Item> resultList2 = EM.createQuery(query2, Item.class).getResultList();

        for (Item itemList : resultList2) {
            System.out.println(itemList.getName());
            System.out.println(itemList.getClass());
        }

        TS.commit();
        EM.close();
        EMF.close();
    }
}
