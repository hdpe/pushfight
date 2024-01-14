package me.hdpe.pushfight.server.persistence.account

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet

@Repository
class AccountJdbcRepository(val jdbcTemplate: JdbcTemplate) {
    fun findAllWithStatistics(): Iterable<AccountWithStatisticsDetails> {
        return jdbcTemplate.query("""
            with completed_games as (
                select games.*
                from games
                inner join accounts p1 on games.player1_account_id=p1.id
                inner join accounts p2 on games.player2_account_id=p2.id
                where 
                    victor_account_id is not null 
                    and p1.exclude_from_statistics = false
                    and p2.exclude_from_statistics = false
            ), results as (
                select player1_account_id as account_id, (case when player1_account_id=victor_account_id then 1 else 0 end) as won, 1 as played
                from completed_games
                union all
                select player2_account_id as account_id, (case when player2_account_id=victor_account_id then 1 else 0 end) as won, 1 as played
                from completed_games
            )
            select
                id,
                username,
                normalized_username,
                coalesce(sum(results.played), 0) as played,
                coalesce(sum(results.won), 0) as won,
                coalesce(sum(results.played) - sum(results.won), 0) as lost
            from accounts
            left join results on accounts.id=results.account_id
            group by id
            order by normalized_username;            
        """) { rs: ResultSet, i: Int ->
            AccountWithStatisticsDetails(
                    rs.getString("id"),
                    rs.getString("username"),
                    rs.getInt("played"),
                    rs.getInt("won"),
                    rs.getInt("lost")
            )
        }
    }
}