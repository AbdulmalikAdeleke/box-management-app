package com.example.polarisdigitech.repository;

import com.example.polarisdigitech.exception.BoxNotFoundException;
import com.example.polarisdigitech.exception.DuplicateBoxException;
import com.example.polarisdigitech.enums.BoxState;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class BoxRepo {

    private static final int MIN_LOADING_BATTERY = 25;

    private final JdbcTemplate jdbcTemplate;

    public BoxRepo(
            JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long createBox(
            String txref,
            int batteryPercentage
    ) {

        try {

            jdbcTemplate.update(
                    """
                    INSERT INTO boxes (
                        txref,
                        current_weight,
                        battery_percentage,
                        state
                    )
                    VALUES (?, 0, ?, ?)
                    """,
                    txref,
                    batteryPercentage,
                    BoxState.IDLE.name()
            );

        } catch (DuplicateKeyException ex) {
            throw new DuplicateBoxException(txref);
        }

        return jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM boxes
                WHERE txref = ?
                """,
                Long.class,
                txref
        );
    }

    /*
     * FOR UPDATE
     *
     * The service calls this inside a transaction.
     * creates a row-lock  to avoid two concurrent threads or operations from updating or reading old/stale data
     */
    public BoxData findAndLockBox(
            String txref
    ) {

        return jdbcTemplate.query(
                """
                SELECT
                    id,
                    txref,
                    current_weight,
                    battery_percentage,
                    state
                FROM boxes
                WHERE txref = ?
                FOR UPDATE
                """,
                rs -> {

                    if (!rs.next()) {
                        throw new BoxNotFoundException(txref);
                    }

                    return mapBox(rs);
                },
                txref
        );
    }

    public BoxData findBox(
            String txref
    ) {

        return jdbcTemplate.query(
                """
                SELECT
                    id,
                    txref,
                    current_weight,
                    battery_percentage,
                    state
                FROM boxes
                WHERE txref = ?
                """,
                rs -> {

                    if (!rs.next()) {
                        throw new BoxNotFoundException(txref);
                    }

                    return mapBox(rs);
                },
                txref
        );
    }

    public void insertItem(
            long boxId,
            String name,
            int weight,
            String code
    ) {

        jdbcTemplate.update(
                """
                INSERT INTO items (
                    name,
                    weight,
                    code,
                    box_id
                )
                VALUES (?, ?, ?, ?)
                """,
                name,
                weight,
                code,
                boxId
        );
    }

    public void updateWeight(
            long boxId,
            int newWeight
    ) {

        jdbcTemplate.update(
                """
                UPDATE boxes
                SET current_weight = ?
                WHERE id = ?
                """,
                newWeight,
                boxId
        );
    }

    public void updateState(
            long boxId,
            BoxState state
    ) {

        jdbcTemplate.update(
                """
                UPDATE boxes
                SET state = ?
                WHERE id = ?
                """,
                state.name(),
                boxId
        );
    }

    public void updateBattery(
            long boxId,
            int batteryPercentage
    ) {

        jdbcTemplate.update(
                """
                UPDATE boxes
                SET battery_percentage = ?
                WHERE id = ?
                """,
                batteryPercentage,
                boxId
        );
    }

    public List<ItemData> getItems(
            String txref
    ) {

        BoxData box = findBox(txref);

        return jdbcTemplate.query(
                """
                SELECT
                    id,
                    name,
                    weight,
                    code
                FROM items
                WHERE box_id = ?
                ORDER BY id
                """,
                (rs, rowNum) ->
                        new ItemData(
                                rs.getLong("id"),
                                rs.getString("name"),
                                rs.getInt("weight"),
                                rs.getString("code")
                        ),
                box.id()
        );
    }

    public List<BoxData> findAvailableBoxes() {

        return jdbcTemplate.query(
                """
                SELECT
                    id,
                    txref,
                    current_weight,
                    battery_percentage,
                    state
                FROM boxes
                WHERE state IN (?, ?)
                  AND battery_percentage >= ?
                ORDER BY id
                """,
                (rs, rowNum) -> mapBox(rs),
                BoxState.IDLE.name(),
                BoxState.LOADING.name(),
                MIN_LOADING_BATTERY
        );
    }

    private BoxData mapBox(
            ResultSet rs
    ) throws SQLException {

        return new BoxData(
                rs.getLong("id"),
                rs.getString("txref"),
                rs.getInt("current_weight"),
                rs.getInt("battery_percentage"),
                BoxState.valueOf(
                        rs.getString("state")
                )
        );
    }

    public record BoxData(
            long id,
            String txref,
            int currentWeight,
            int batteryPercentage,
            BoxState state
    ) {
    }

    public record ItemData(
            long id,
            String name,
            int weight,
            String code
    ) {
    }
}