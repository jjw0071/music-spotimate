package kodong.spotimate.authentication.repository;

import kodong.spotimate.authentication.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Members, String> {

    // user_id로 검색하도록 메서드 추가
    Optional<Members> findByUserId(String userId);

}
