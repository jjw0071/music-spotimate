package kodong.spotimate.authentication.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Members {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false) // not null 제약 조건 추가
    private String userId;

    private String email;

    private String displayName;

}
