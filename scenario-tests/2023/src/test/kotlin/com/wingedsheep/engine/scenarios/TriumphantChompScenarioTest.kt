package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.DamageComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Color
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Scenario tests for Triumphant Chomp (LCI #170) — {R} Sorcery.
 *
 * Oracle: "Triumphant Chomp deals damage to target creature equal to 2 or the greatest power
 * among Dinosaurs you control, whichever is greater."
 *
 * The amount is `Max(Fixed(2), maxPower over Dinosaur creatures you control)`:
 *  - No Dinosaurs → the aggregate is 0, so the floor of 2 wins.
 *  - A Dinosaur with power > 2 → its power wins.
 *  - A Dinosaur with power < 2 → the floor of 2 still wins.
 *
 * Each test targets a 0/20 wall so the creature survives and the exact marked-damage amount can be
 * read off its [DamageComponent].
 */
class TriumphantChompScenarioTest : FunSpec({

    // A big vanilla wall to absorb (and survive) the damage so we can read the exact amount.
    val bigWall = CardDefinition.creature(
        name = "Test Wall",
        manaCost = ManaCost.parse("{0}"),
        subtypes = emptySet(),
        power = 0,
        toughness = 20,
    )

    // A Dinosaur with power 5 — the "greatest power among Dinosaurs you control" source.
    val bigDinosaur = CardDefinition.creature(
        name = "Test Raptor",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype.DINOSAUR),
        power = 5,
        toughness = 5,
    )

    // A Dinosaur with power 1 — below the floor of 2, so it must not lower the damage.
    val tinyDinosaur = CardDefinition.creature(
        name = "Test Hatchling",
        manaCost = ManaCost.parse("{0}"),
        subtypes = setOf(Subtype.DINOSAUR),
        power = 1,
        toughness = 1,
    )

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        // TestCards.all covers every catalog card (Triumphant Chomp included); only the
        // test-local creatures need explicit registration.
        driver.registerCards(TestCards.all)
        driver.registerCard(bigWall)
        driver.registerCard(bigDinosaur)
        driver.registerCard(tinyDinosaur)
        return driver
    }

    fun GameTestDriver.markedDamage(id: EntityId): Int =
        state.getEntity(id)?.get<DamageComponent>()?.amount ?: 0

    test("no Dinosaurs → deals the floor of 2 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val wall = driver.putCreatureOnBattlefield(opponent, "Test Wall")
        val chomp = driver.putCardInHand(me, "Triumphant Chomp")
        driver.giveMana(me, Color.RED, 1)

        driver.castSpell(me, chomp, listOf(wall))
        driver.bothPass()

        driver.markedDamage(wall) shouldBe 2
    }

    test("greatest Dinosaur power (5) beats the floor → deals 5 damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(me, "Test Raptor") // power 5 Dinosaur I control
        val wall = driver.putCreatureOnBattlefield(opponent, "Test Wall")
        val chomp = driver.putCardInHand(me, "Triumphant Chomp")
        driver.giveMana(me, Color.RED, 1)

        driver.castSpell(me, chomp, listOf(wall))
        driver.bothPass()

        driver.markedDamage(wall) shouldBe 5
    }

    test("Dinosaur power below the floor (1) → floor of 2 still wins") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(me, "Test Hatchling") // power 1 Dinosaur I control
        val wall = driver.putCreatureOnBattlefield(opponent, "Test Wall")
        val chomp = driver.putCardInHand(me, "Triumphant Chomp")
        driver.giveMana(me, Color.RED, 1)

        driver.castSpell(me, chomp, listOf(wall))
        driver.bothPass()

        driver.markedDamage(wall) shouldBe 2
    }

    test("only Dinosaurs YOU control count — opponent's big Dinosaur is ignored") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40), startingLife = 20)
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCreatureOnBattlefield(opponent, "Test Raptor") // opponent's power 5 Dinosaur
        val wall = driver.putCreatureOnBattlefield(opponent, "Test Wall")
        val chomp = driver.putCardInHand(me, "Triumphant Chomp")
        driver.giveMana(me, Color.RED, 1)

        driver.castSpell(me, chomp, listOf(wall))
        driver.bothPass()

        // My board has no Dinosaurs, so the floor of 2 applies.
        driver.markedDamage(wall) shouldBe 2
    }
})
