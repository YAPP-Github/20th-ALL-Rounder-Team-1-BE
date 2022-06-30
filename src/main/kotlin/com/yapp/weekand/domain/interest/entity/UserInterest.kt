package com.yapp.weekand.domain.interest.entity

import com.yapp.weekand.domain.user.entity.User
import javax.persistence.*

@Entity
class UserInterest(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_interest_id")
        val id: Long? = null,

        var interestName: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        var user: User
) {
	companion object{
		fun of(user: User, interest:String): UserInterest {
			return UserInterest(user = user, interestName = interest)
		}
	}
}
