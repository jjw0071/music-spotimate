package kodong.spotimate.authentication.repository;

import kodong.spotimate.authentication.domain.Members;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Members, String> {
}
