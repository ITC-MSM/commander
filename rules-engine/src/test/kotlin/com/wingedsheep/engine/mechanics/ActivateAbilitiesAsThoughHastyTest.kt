package com.wingedsheep.engine.mechanics

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.DeclareAttackers
import com.wingedsheep.engine.legalactions.LegalAction
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Engine coverage for [AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY] — the Thousand-Year
 * Elixir / Shang-Chi permission, "you may activate abilities of creatures you control as though
 * those creatures had haste".
 *
 * CR 302.6 gates two things on the same condition: a creature's activated ability whose cost
 * includes the tap or untap symbol, **and** attacking. Haste (CR 702.10b/c) lifts both. This flag
 * lifts only the first, so the interesting assertions are the *pair*: the `{T}` ability becomes
 * legal while the attack stays illegal. Every positive tap-symbol assertion goes through
 * `legalActions` — the enumerator is what the client is offered, and submitting the action directly
 * would skip it entirely.
 *
 * Every board is built inside the turn it is tested in, never across an untap step — an untap step
 * in between clears the sickness marker and makes the assertions vacuous. Main-phase tests build
 * after advancing. The combat tests build first and then advance within the same turn, because the
 * engine skips the declare-attackers step entirely when the active player has no *legal* attacker:
 * with only a sick creature on board, `passPriorityUntil(DECLARE_ATTACKERS)` rolls forward to a
 * later turn and the marker is gone by the time it arrives. Hence the printed-haste creature that
 * opens combat in the sick-creature test.
 */
class ActivateAbilitiesAsThoughHastyTest : FunSpec({

    // =========================================================================
    // Test cards
    // =========================================================================

    /** The permission, isolated from Shang-Chi so this file tests the primitive, not the card. */
    val elixir = card("Test Haste Elixir") {
        manaCost = "{2}"
        typeLine = "Artifact"
        oracleText = "You may activate abilities of creatures you control as though those " +
            "creatures had haste."
        staticAbility {
            ability = GrantKeyword(
                AbilityFlag.MAY_ACTIVATE_ABILITIES_AS_THOUGH_HASTY.name,
                GroupFilter.AllCreaturesYouControl,
            )
        }
    }

    /** A creature whose only ability costs `{T}` — the CR 302.6 tap-symbol case. */
    val tapper = card("Test Tapper") {
        manaCost = "{1}"
        typeLine = "Creature — Human Monk"
        power = 2
        toughness = 2
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(1)
            description = "{T}: You gain 1 life."
        }
    }

    /** A creature whose only ability costs `{Q}` — the untap symbol, gated by the same rule. */
    val untapper = card("Test Untapper") {
        manaCost = "{1}"
        typeLine = "Creature — Human Monk"
        power = 2
        toughness = 2
        activatedAbility {
            cost = Costs.Untap
            effect = Effects.GainLife(1)
            description = "{Q}: You gain 1 life."
        }
    }

    /** A creature whose ability has no tap/untap symbol — CR 302.6 never touches it. */
    val bleeder = card("Test Bleeder") {
        manaCost = "{1}"
        typeLine = "Creature — Human Monk"
        power = 2
        toughness = 2
        activatedAbility {
            cost = Costs.PayLife(1)
            effect = Effects.GainLife(2)
            description = "Pay 1 life: You gain 2 life."
        }
    }

    /** Same `{T}` ability, but the creature has printed haste — the control for the pair. */
    val hastyTapper = card("Test Hasty Tapper") {
        manaCost = "{1}"
        typeLine = "Creature — Human Monk"
        power = 2
        toughness = 2
        keywords(Keyword.HASTE)
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.GainLife(1)
            description = "{T}: You gain 1 life."
        }
    }

    val allTestCards = listOf(elixir, tapper, untapper, bleeder, hastyTapper)

    // =========================================================================
    // Helpers
    // =========================================================================

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + allTestCards)
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40))
        return driver
    }

    /** Advance until [target] with player 1 the active player, cycling whole turns if needed. */
    fun GameTestDriver.advanceToPlayer1(target: Step) {
        passPriorityUntil(target)
        var safety = 0
        while (activePlayer != player1 && safety < 50) {
            bothPass()
            passPriorityUntil(target)
            safety++
        }
        activePlayer shouldBe player1
    }

    fun List<LegalAction>.activationsOf(sourceId: EntityId): List<LegalAction> =
        filter { (it.action as? ActivateAbility)?.sourceId == sourceId }

    fun List<LegalAction>.offeredAttackers(): List<EntityId> =
        firstOrNull { it.actionType == "DeclareAttackers" }?.validAttackers ?: emptyList()

    fun GameTestDriver.abilityIdOf(cardName: String) =
        cardRegistry.requireCard(cardName).script.activatedAbilities.single().id

    // =========================================================================
    // The tap-symbol half — lifted
    // =========================================================================

    test("a summoning-sick creature's {T} ability is not offered without the permission") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Tapper")

        driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 0
    }

    test("the permission makes a summoning-sick creature's {T} ability legal") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Tapper")
        driver.putPermanentOnBattlefield(driver.player1, "Test Haste Elixir")

        val offered = driver.legalActions(driver.player1).activationsOf(creature)
        withClue("the {T} ability should be enumerated under the as-though-hasty permission") {
            offered.size shouldBe 1
        }

        // And it actually resolves — the enumerator and the authoritative re-check agree.
        val lifeBefore = driver.getLifeTotal(driver.player1)
        driver.submitSuccess(offered.single().action)
        driver.bothPass()
        driver.getLifeTotal(driver.player1) shouldBe lifeBefore + 1
    }

    test("the permission does not reach a creature its controller does not control") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        // Player 2's Elixir says "creatures you control" — player 1's creature is not covered.
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Tapper")
        driver.putPermanentOnBattlefield(driver.player2, "Test Haste Elixir")

        driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 0
    }

    // =========================================================================
    // The attacking half — NOT lifted
    // =========================================================================

    test("the permission does not let a summoning-sick creature attack") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Tapper")
        driver.putPermanentOnBattlefield(driver.player1, "Test Haste Elixir")
        // The engine skips the declare-attackers step outright when the active player has no legal
        // attacker, so a creature with *printed* haste has to open combat — otherwise priority
        // passing rolls on to a later turn whose untap step clears the sickness marker and the test
        // silently asserts nothing. It doubles as the in-list control: real haste is offered from
        // the very same list that must not contain the sick creature.
        val hasty = driver.putCreatureOnBattlefield(driver.player1, "Test Hasty Tapper")
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        val attackers = driver.legalActions(driver.player1).offeredAttackers()
        withClue("as-though-hasty must not grant haste — attacking stays illegal (CR 302.6)") {
            attackers.contains(creature) shouldBe false
        }
        withClue("...while printed haste is offered, from the same enumeration") {
            attackers.contains(hasty) shouldBe true
        }

        // The authoritative handler must agree with the enumerator.
        driver.submitExpectFailure(
            DeclareAttackers(
                playerId = driver.player1,
                attackers = mapOf(creature to driver.player2)
            )
        )
    }

    test("printed haste lifts both halves — the control for the pair") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Hasty Tapper")
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)

        withClue("real haste still lets a freshly-arrived creature attack") {
            driver.legalActions(driver.player1).offeredAttackers().contains(creature) shouldBe true
        }
    }

    // =========================================================================
    // Abilities the rule never gated
    // =========================================================================

    test("an ability with no tap or untap symbol is legal for a summoning-sick creature either way") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Bleeder")

        withClue("CR 302.6 only gates the tap and untap symbols") {
            driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 1
        }

        driver.putPermanentOnBattlefield(driver.player1, "Test Haste Elixir")
        driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 1
    }

    // =========================================================================
    // The untap symbol
    // =========================================================================

    // `{Q}` is gated in both places, like `{T}`: `ActivatedAbilityEnumerator` has an
    // `AbilityCost.Untap` branch and `ActivateAbilityHandler` re-checks authoritatively. No shipped
    // card uses `Costs.Untap`, so these tests are the only coverage the branch has — each asserts
    // through `legalActions` *and* drives the handler, so a regression in either one goes red.
    test("a summoning-sick creature's {Q} ability is rejected without the permission") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Untapper")
        driver.tapPermanent(creature) // {Q} needs a tapped permanent to untap.

        withClue("the enumerator must not offer a sick creature's {Q} ability") {
            driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 0
        }

        val result = driver.submitExpectFailure(
            ActivateAbility(
                playerId = driver.player1,
                sourceId = creature,
                abilityId = driver.abilityIdOf("Test Untapper")
            )
        )
        result.error shouldBe "This creature has summoning sickness"
    }

    test("the permission makes a summoning-sick creature's {Q} ability activatable") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Untapper")
        driver.tapPermanent(creature)
        driver.putPermanentOnBattlefield(driver.player1, "Test Haste Elixir")

        val offered = driver.legalActions(driver.player1).activationsOf(creature)
        withClue("the {Q} ability should be enumerated under the as-though-hasty permission") {
            offered.size shouldBe 1
        }

        val lifeBefore = driver.getLifeTotal(driver.player1)
        driver.submitSuccess(offered.single().action)
        driver.bothPass()
        driver.getLifeTotal(driver.player1) shouldBe lifeBefore + 1
    }

    test("an untapped creature's {Q} ability is never offered — the permission doesn't change that") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        // Left untapped on purpose: `{Q}` means "untap this permanent", so an untapped source can't
        // pay it at all. This pins the enumerator's tapped-check, which is inverted relative to {T}.
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Untapper")
        driver.putPermanentOnBattlefield(driver.player1, "Test Haste Elixir")

        withClue("summoning sickness is lifted, but the untap symbol still needs a tapped permanent") {
            driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 0
        }
    }

    // =========================================================================
    // The permission leaving play
    // =========================================================================

    test("the {T} ability stops being offered when the permission leaves play mid-turn") {
        val driver = createDriver()
        driver.advanceToPlayer1(Step.PRECOMBAT_MAIN)
        val creature = driver.putCreatureOnBattlefield(driver.player1, "Test Tapper")
        val elixirId = driver.putPermanentOnBattlefield(driver.player1, "Test Haste Elixir")

        driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 1

        driver.moveToGraveyard(elixirId)

        withClue("the grant is a continuous effect — it dies with its source") {
            driver.legalActions(driver.player1).activationsOf(creature).size shouldBe 0
        }
    }
})
