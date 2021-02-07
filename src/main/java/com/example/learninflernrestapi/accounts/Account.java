package com.example.learninflernrestapi.accounts;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account { //Account를 Event에서 단방향으로 참조할 수 있도록! (즉, Event에서만 Owner를 참조할 수 있도록! 단방향 맵핑을 하겠다!)

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String email;

    private String password;

    @ElementCollection(fetch = FetchType.EAGER) // 하나의 Enum만 있는게 아니라 여러개의 Enum을 가질 수 있기 때문에 사용! (default는 lazy이다)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;
}
