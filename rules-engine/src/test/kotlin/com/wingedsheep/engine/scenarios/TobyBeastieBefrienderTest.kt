package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.identity.CardComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.dsk.cards.FearOfBeingHunted
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardDefinition
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.CantBlockUnlessCoBlocker
import com.wingedsheep.sdk.scripting.CantBeBlockedByMoreThan
import com.wingedsheep.sdk.scripting.CanBlockAnyNumber
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.MustBeBlocked
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Tests for Toby, Beastie Befriender (DSK #35) and the new "can't block alone" combat restriction
 * ([CantBlockUnlessCoBlocker], CR 509.1b — the blocking sibling of `CantAttackUnlessCoAttacker`,
 * CR 508.1c).
 *
 * Toby:
 * - ETB: create a 4/4 white Beast token with "This token can't attack or block alone."
 * - Static: "As long as you control four or more creature tokens, creature tokens you control
 *   have flying."
 */
class TobyBeastieBefrienderTest : FunSpec({

    // A vanilla creature used as a co-attacker / co-blocker partner.
    val TestWarrior = CardDefinition.creature(
        name = "Test Warrior",
        manaCost = ManaCost.parse("{1}{R}"),
        subtypes = setOf(Subtype("Human"), Subtype("Warrior")),
        power = 2,
        toughness = 2,
        oracleText = ""
    )

    // A bare-bones "can't block alone" creature CARD, so the restriction is exercised through the
    // card-registry path (CR 509.1b) independently of token plumbing.
    val LonelySentinel = card("Lonely Sentinel") {
        manaCost = "{W}"
        typeLine = "Creature — Soldier"
        power = 2
        toughness = 2
        oracleText = "This creature can't block alone."
        staticAbility {
            ability = CantBlockUnlessCoBlocker(coBlockerFilter = GameObjectFilter.Creature)
        }
    }

    val SolitaryDemand = card("Solitary Demand") {
        manaCost = "{R}"
        typeLine = "Creature — Beast"
        power = 3
        toughness = 3
        oracleText = "This creature must be blocked if able.\nThis creature can't be blocked by more than one creature."
        staticAbility { ability = MustBeBlocked(allCreatures = false) }
        staticAbility { ability = CantBeBlockedByMoreThan(maxBlockers = 1) }
    }

    val ManyHandsSentinel = card("Many-Hands Sentinel") {
        manaCost = "{W}"
        typeLine = "Creature — Soldier"
        power = 2
        toughness = 2
        oracleText = "This creature can't block alone.\nThis creature can block any number of creatures."
        staticAbility { ability = CantBlockUnlessCoBlocker(coBlockerFilter = GameObjectFilter.Creature) }
        staticAbility { ability = CanBlockAnyNumber() }
    }

    // Token makers, used to drive Toby's "four or more creature tokens" static ability.
    val MakeThreeSaprolings = card("Make Three Saprolings") {
        manaCost = "{G}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.CreateToken(
                power = 1, toughness = 1, colors = setOf(Color.GREEN),
                creatureTypes = setOf("Saproling"), count = 3
            )
        }
    }
    val MakeFourSaprolings = card("Make Four Saprolings") {
        manaCost = "{G}"
        typeLine = "Sorcery"
        spell {
            effect = Effects.CreateToken(
                power = 1, toughness = 1, colors = setOf(Color.GREEN),
                creatureTypes = setOf("Saproling"), count = 4
            )
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(
                TestWarrior, LonelySentinel, SolitaryDemand, ManyHandsSentinel, MakeThreeSaprolings, MakeFourSaprolings,
                FearOfBeingHunted
            )
        )
        return driver
    }

    fun GameTestDriver.beastTokenOf(playerId: EntityId): EntityId? =
        state.getBattlefield().firstOrNull { id ->
            state.getEntity(id)?.get<CardComponent>()?.name == "Beast" && getController(id) == playerId
        }

    fun GameTestDriver.saprolingOf(playerId: EntityId): EntityId? =
        state.getBattlefield().firstOrNull { id ->
            state.getEntity(id)?.get<CardComponent>()?.name?.contains("Saproling") == true &&
                getController(id) == playerId
        }

    fun GameTestDriver.castTobyAndResolve(playerId: EntityId): EntityId {
        giveMana(playerId, Color.WHITE, 3)
        val toby = putCardInHand(playerId, "Toby, Beastie Befriender")
        val cast = castSpell(playerId, toby)
        withClue("Casting Toby should succeed") { cast.error shouldBe null }
        // Resolve Toby itself, then its ETB trigger.
        bothPass()
        bothPass()
        return beastTokenOf(playerId) ?: error("Toby's ETB Beast token was not created")
    }

    // -------------------------------------------------------------------------
    // can't block alone (CR 509.1b) — card-registry path
    // -------------------------------------------------------------------------

    test("a 'can't block alone' creature cannot be the only blocker") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        // p2 is the defender; give it the lonely blocker plus a partner.
        val sentinel = driver.putCreatureOnBattlefield(p2, "Lonely Sentinel")
        val partner = driver.putCreatureOnBattlefield(p2, "Test Warrior")
        // p1 attacks with a creature it has controlled since before this turn.
        val attacker = driver.putCreatureOnBattlefield(p1, "Test Warrior")
        driver.removeSummoningSickness(attacker)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(attacker), p2)
        driver.bothPass()

        withClue("Lonely Sentinel can't block alone") {
            driver.declareBlockers(p2, mapOf(sentinel to listOf(attacker))).error shouldNotBe null
        }
        withClue("…but can block alongside another blocker") {
            driver.declareBlockers(
                p2, mapOf(sentinel to listOf(attacker), partner to listOf(attacker))
            ).error shouldBe null
        }
    }

    test("a lone 'can't block alone' creature does not make a must-be-blocked attacker able to be blocked") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val attacker = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        driver.removeSummoningSickness(attacker)
        driver.putCreatureOnBattlefield(p2, "Lonely Sentinel")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(attacker), p2).error shouldBe null
        driver.bothPass()

        // The Sentinel cannot legally block on its own. 509.1c therefore permits the empty
        // declaration even though the attacker has "must be blocked if able".
        driver.declareBlockers(p2, emptyMap()).error shouldBe null
    }

    test("a co-blocker can support a must-block assignment against a different attacker") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val firstAttacker = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        val secondAttacker = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        driver.removeSummoningSickness(firstAttacker)
        driver.removeSummoningSickness(secondAttacker)
        val sentinel = driver.putCreatureOnBattlefield(p2, "Lonely Sentinel")
        val partner = driver.putCreatureOnBattlefield(p2, "Test Warrior")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(firstAttacker, secondAttacker), p2).error shouldBe null
        driver.bothPass()

        // Two requirements can be obeyed: Sentinel's partner need not block the same attacker.
        driver.declareBlockers(p2, emptyMap()).error shouldNotBe null
        driver.declareBlockers(
            p2, mapOf(sentinel to listOf(firstAttacker), partner to listOf(secondAttacker))
        ).error shouldBe null
    }

    test("a blocker cap can make a co-blocker-restricted must-block requirement impossible") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val attacker = driver.putCreatureOnBattlefield(p1, "Solitary Demand")
        driver.removeSummoningSickness(attacker)
        val sentinel = driver.putCreatureOnBattlefield(p2, "Lonely Sentinel")
        val partner = driver.putCreatureOnBattlefield(p2, "Lonely Sentinel")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(attacker), p2).error shouldBe null
        driver.bothPass()

        // The two Sentinels can support one another only if both block, but the attacker permits
        // only one blocker. No legal declaration can satisfy the must-block requirement.
        driver.declareBlockers(p2, emptyMap()).error shouldBe null
        driver.declareBlockers(p2, mapOf(sentinel to listOf(attacker))).error shouldNotBe null
        driver.declareBlockers(p2, mapOf(partner to listOf(attacker))).error shouldNotBe null
    }

    test("a co-blocker-restricted creature that can block any number covers every attainable must-block attacker") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val firstAttacker = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        val secondAttacker = driver.putCreatureOnBattlefield(p1, "Fear of Being Hunted")
        driver.removeSummoningSickness(firstAttacker)
        driver.removeSummoningSickness(secondAttacker)
        val sentinel = driver.putCreatureOnBattlefield(p2, "Many-Hands Sentinel")
        val partner = driver.putCreatureOnBattlefield(p2, "Test Warrior")

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(firstAttacker, secondAttacker), p2).error shouldBe null
        driver.bothPass()

        driver.declareBlockers(
            p2, mapOf(sentinel to listOf(firstAttacker), partner to listOf(firstAttacker))
        ).error shouldNotBe null
        driver.declareBlockers(
            p2, mapOf(sentinel to listOf(firstAttacker, secondAttacker), partner to listOf(firstAttacker))
        ).error shouldBe null
    }

    // -------------------------------------------------------------------------
    // Toby's Beast token — restriction arrives via grantedStaticAbilities (no CardDefinition)
    // -------------------------------------------------------------------------

    test("Toby's Beast token can't attack alone but can with a co-attacker") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val beast = driver.castTobyAndResolve(p1)
        driver.removeSummoningSickness(beast)
        val ally = driver.putCreatureOnBattlefield(p1, "Test Warrior")
        driver.removeSummoningSickness(ally)

        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        withClue("Beast token can't attack alone") {
            driver.declareAttackers(p1, listOf(beast), p2).error shouldNotBe null
        }
        withClue("…but can attack alongside another attacker") {
            driver.declareAttackers(p1, listOf(beast, ally), p2).error shouldBe null
        }
    }

    test("Toby's Beast token can't block alone but can with a co-blocker") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Mountain" to 20), startingLife = 20)
        val p1 = driver.player1
        val p2 = driver.player2

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val beast = driver.castTobyAndResolve(p1)
        val ally = driver.putCreatureOnBattlefield(p1, "Test Warrior")
        // p2's attacker, controlled since before p2's turn.
        val attacker = driver.putCreatureOnBattlefield(p2, "Test Warrior")

        // Advance to p2's declare-attackers (past p1's own combat, which auto-submits empty).
        driver.passPriorityUntil(Step.END)
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p2, listOf(attacker), p1)
        driver.bothPass()

        withClue("Beast token can't block alone") {
            driver.declareBlockers(p1, mapOf(beast to listOf(attacker))).error shouldNotBe null
        }
        withClue("…but can block alongside another blocker") {
            driver.declareBlockers(
                p1, mapOf(beast to listOf(attacker), ally to listOf(attacker))
            ).error shouldBe null
        }
    }

    // -------------------------------------------------------------------------
    // Toby's flying static ability (gated on four or more creature tokens)
    // -------------------------------------------------------------------------

    test("creature tokens you control don't have flying with only three creature tokens") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Forest" to 20), startingLife = 20)
        val p1 = driver.player1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Toby, Beastie Befriender") // static source (no ETB token)
        driver.giveMana(p1, Color.GREEN, 1)
        driver.castSpell(p1, driver.putCardInHand(p1, "Make Three Saprolings"))
        driver.bothPass()

        val token = driver.saprolingOf(p1) ?: error("No Saproling token")
        withClue("3 creature tokens < 4 → no flying") {
            driver.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe false
        }
    }

    test("creature tokens you control gain flying with four or more creature tokens") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 20, "Forest" to 20), startingLife = 20)
        val p1 = driver.player1

        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        driver.putCreatureOnBattlefield(p1, "Toby, Beastie Befriender") // static source (no ETB token)
        driver.giveMana(p1, Color.GREEN, 1)
        driver.castSpell(p1, driver.putCardInHand(p1, "Make Four Saprolings"))
        driver.bothPass()

        val token = driver.saprolingOf(p1) ?: error("No Saproling token")
        withClue("4 creature tokens ≥ 4 → flying") {
            driver.state.projectedState.hasKeyword(token, Keyword.FLYING) shouldBe true
        }
    }
})
