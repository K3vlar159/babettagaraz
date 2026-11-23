package sk.babettagaraz.domain.forum;

import sk.babettagaraz.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {

    List<ForumThread> findByAuthor(User author);
    List<ForumThread> findByCategory(ForumCategory category);
    List<ForumThread> findByPinnedTrue();
}
