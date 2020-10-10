package hu.bme.aut.android.gifthing.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["userId", "teamId"])
data class UserTeamCrossRef(
    val userId: Long,
    val teamId: Long
)

data class UserWithTeams(
    @Embedded val user: User,
    @Relation(
        parentColumn = "userId",
        entityColumn = "teamId",
        associateBy = Junction(UserTeamCrossRef::class)
    )
    val teams: List<Team>
)

data class TeamWithMembers(
    @Embedded val team: Team,
    @Relation(
        parentColumn = "teamId",
        entityColumn = "userId",
        associateBy = Junction(UserTeamCrossRef::class)
    )
    val members: List<User>
)