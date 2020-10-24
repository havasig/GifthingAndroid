package hu.bme.aut.android.gifthing.database.entities

import androidx.room.*

@Entity(primaryKeys = ["user_id", "team_id"])
data class UserTeamCrossRef(
    @ColumnInfo(name = "user_id") val userId: Long,
    @ColumnInfo(name = "team_id") val teamId: Long
)

data class UserWithTeams(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "team_id",
        associateBy = Junction(UserTeamCrossRef::class)
    )
    val teams: List<Team>
)

data class TeamWithMembers(
    @Embedded val team: Team,
    @Relation(
        parentColumn = "team_id",
        entityColumn = "user_id",
        associateBy = Junction(UserTeamCrossRef::class)
    )
    val members: List<User>
)