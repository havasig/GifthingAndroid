package hu.bme.aut.android.gifthing.database.models.entities

import androidx.room.*

@Entity(primaryKeys = ["user_client_id", "team_client_id"])
data class UserTeamCrossRef(
    @ColumnInfo(name = "user_client_id") val userClientId: Long,
    @ColumnInfo(name = "team_client_id") val teamId: Long
)

data class UserWithTeams(
    @Embedded val user: User,
    @Relation(
        parentColumn = "user_client_id",
        entityColumn = "team_client_id",
        associateBy = Junction(UserTeamCrossRef::class)
    )
    val teams: List<Team>
)

data class TeamWithMembers(
    @Embedded val team: Team,
    @Relation(
        parentColumn = "team_client_id",
        entityColumn = "user_client_id",
        associateBy = Junction(UserTeamCrossRef::class)
    )
    val members: List<User>
)