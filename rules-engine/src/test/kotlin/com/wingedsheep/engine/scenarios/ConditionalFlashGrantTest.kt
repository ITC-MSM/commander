package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CastSpell
import com.wingedsheep.engine.state.components.battlefield.PhasedOutComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantFlashToSpellType
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * A [GrantFlashToSpellType] wrapped in a `ConditionalStaticAbility` — what
 * `staticAbility { condition = … }` produces for an "**as long as** …" flash grant (Captain
 * Mar-Vell, Space-Born).
 *
 * Flash permission never reaches the layer system (`StaticAbilityHandler` deliberately lowers
 * [GrantFlashToSpellType] to no continuous effect), so the wrapper has to be unwrapped by the two
 * read sites themselves. Before that unwrapping existed a gated grant was **silently inert** — it
 * granted flash never rather than conditionally. Both read sites now share one implementation in
 * `FlashTypeGrants`, and these tests pin the gate's whole life cycle against it:
 *
 *  - closed gate → the spell is not even offered, and casting it is rejected;
 *  - gate opening mid-turn → the offer appears without anything else changing;
 *  - open gate → the offer appears *and* the cast is accepted (the second read site,
 *    `CastZoneResolver`, re-checks authoritatively at cast time and would otherwise reject what
 *    enumeration offered);
 *  - the grant's own filter still applies — an open gate doesn't hand flash to every spell type;
 *  - the gate is asked about the *granter's controller's* board, not the caster's (both directions);
 *  - a stolen granter grants to its new controller, and stops granting to its owner;
 *  - a phased-out granter grants nothing (CR 702.26b);
 *  - the granter leaving the battlefield revokes the permission even with the gate still open.
 *
 * Every assertion goes through `legalActions` or the cast handler, never through the static
 * ability list, so a grant that is present-but-unread fails these tests.
 *
 * The board trick is the one [PrintedSuspendTimingTest] uses: cast Lightning Bolt first. The caster
 * keeps priority, but the stack is no longer empty, so sorcery-speed casting is illegal from that
 * moment on — any creature cast still offered is therefore a flash cast.
 */
class ConditionalFlashGrantTest : FunSpec({

    // "As long as you control a Test Flash Enabler, you may cast Sliver spells as though they had
    // flash." The gate is board state so it can be flipped mid-turn without passing priority; the
    // filter is deliberately narrower than "any spell" so the no-leak case is testable.
    val conditionalGranter = card("Test Conditional Flash Granter") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Wizard"
        power = 1
        toughness = 1
        oracleText = "As long as you control a Test Flash Enabler, you may cast Sliver spells " +
            "as though they had flash."
        staticAbility {
            condition = Conditions.YouControl(GameObjectFilter.Any.named("Test Flash Enabler"))
            ability = GrantFlashToSpellType(
                filter = GameObjectFilter.Any.withSubtype("Sliver"),
                controllerOnly = true
            )
        }
    }

    // The same gate without `controllerOnly`, so the grant reaches every player. That is what makes
    // "whose context is the condition evaluated in?" observable: the gate reads *the granter's
    // controller's* board, never the caster's.
    val sharedGranter = card("Test Shared Flash Granter") {
        manaCost = "{1}{U}"
        typeLine = "Creature — Wizard"
        power = 1
        toughness = 1
        oracleText = "As long as you control a Test Flash Enabler, each player may cast Sliver " +
            "spells as though they had flash."
        staticAbility {
            condition = Conditions.YouControl(GameObjectFilter.Any.named("Test Flash Enabler"))
            ability = GrantFlashToSpellType(
                filter = GameObjectFilter.Any.withSubtype("Sliver"),
                controllerOnly = false
            )
        }
    }

    // A Threaten-on-an-instant, so a granter can change hands without the stack emptying.
    val steal = card("Test Steal") {
        manaCost = "{1}{U}"
        typeLine = "Instant"
        oracleText = "Gain control of target creature."
        spell {
            val t = target("target creature", Targets.Creature)
            effect = Effects.GainControl(t, Duration.Permanent)
        }
    }

    val enabler = card("Test Flash Enabler") {
        manaCost = "{1}"
        typeLine = "Creature — Construct"
        power = 1
        toughness = 1
    }

    val gatedSliver = card("Test Gated Sliver") {
        manaCost = "{R}"
        typeLine = "Creature — Sliver"
        power = 1
        toughness = 1
    }

    val gatedGoblin = card("Test Gated Goblin") {
        manaCost = "{R}"
        typeLine = "Creature — Goblin"
        power = 1
        toughness = 1
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(conditionalGranter, sharedGranter, steal, enabler, gatedSliver, gatedGoblin)
        )
        driver.initMirrorMatch(deck = Deck.of("Mountain" to 40))
        return driver
    }

    /**
     * Sets the board up in the precombat main phase with [me] holding priority, a non-empty stack
     * (so sorcery speed is illegal), the conditional granter in play, and enough red mana floating
     * to pay for one {R} creature. The gate starts **closed** — no enabler on the battlefield.
     */
    fun openingPosition(driver: GameTestDriver): EntityId {
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(me, "Test Conditional Flash Granter")

        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        // Two red: one burnt on the Bolt that occupies the stack, one left for the creature, so
        // affordability is never what decides these assertions.
        driver.giveMana(me, Color.RED, 2)
        driver.castSpell(me, bolt, listOf(opponent)).isSuccess shouldBe true
        driver.state.stack.isEmpty() shouldBe false
        driver.state.priorityPlayerId shouldBe me
        return me
    }

    fun castOffered(driver: GameTestDriver, playerId: EntityId, cardId: EntityId): Boolean =
        driver.legalActions(playerId).any { legal ->
            (legal.action as? CastSpell)?.cardId == cardId
        }

    test("a gated flash grant offers nothing while its condition is false") {
        val driver = createDriver()
        val me = openingPosition(driver)
        val sliver = driver.putCardInHand(me, "Test Gated Sliver")

        withClue("no enabler on the battlefield, so the grant's condition is false") {
            castOffered(driver, me, sliver) shouldBe false
        }
        withClue("and the cast handler agrees — this is the authoritative read site") {
            driver.submit(
                CastSpell(playerId = me, cardId = sliver, paymentStrategy = com.wingedsheep.engine.core.PaymentStrategy.FromPool)
            ).isSuccess shouldBe false
        }
    }

    test("the grant switches on mid-turn the moment its condition becomes true") {
        val driver = createDriver()
        val me = openingPosition(driver)
        val sliver = driver.putCardInHand(me, "Test Gated Sliver")

        castOffered(driver, me, sliver) shouldBe false

        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")

        withClue("nothing changed but the gate, and the flash cast is now offered") {
            castOffered(driver, me, sliver) shouldBe true
        }
    }

    test("with the condition true the spell is both offered and actually castable") {
        val driver = createDriver()
        val me = openingPosition(driver)
        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")
        val sliver = driver.putCardInHand(me, "Test Gated Sliver")

        castOffered(driver, me, sliver) shouldBe true
        withClue("CastZoneResolver re-checks the grant at cast time and must reach the same answer") {
            driver.castSpell(me, sliver).isSuccess shouldBe true
        }
    }

    test("an open gate does not leak flash to spells outside the grant's filter") {
        val driver = createDriver()
        val me = openingPosition(driver)
        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")
        val sliver = driver.putCardInHand(me, "Test Gated Sliver")
        val goblin = driver.putCardInHand(me, "Test Gated Goblin")

        withClue("control: the gate really is open") {
            castOffered(driver, me, sliver) shouldBe true
        }
        withClue("the Goblin matches neither the filter nor sorcery-speed timing") {
            castOffered(driver, me, goblin) shouldBe false
        }
    }

    /**
     * Hands [caster] priority with the Bolt still on the stack (so sorcery speed stays illegal) and
     * one red floating, holding a Sliver. Returns that Sliver.
     */
    fun armOtherSeat(driver: GameTestDriver, caster: EntityId): EntityId {
        driver.passPriority(driver.state.priorityPlayerId!!)
        driver.state.priorityPlayerId shouldBe caster
        driver.giveMana(caster, Color.RED, 1)
        return driver.putCardInHand(caster, "Test Gated Sliver")
    }

    test("the gate reads the granter's controller's board, not the caster's") {
        val driver = createDriver()
        val me = openingPosition(driver)
        val opponent = driver.getOpponent(me)
        // A second, ungated-by-controller granter: the permission reaches the opponent, but its
        // condition still has to be asked about *my* board, since I control the granter.
        driver.putPermanentOnBattlefield(me, "Test Shared Flash Granter")
        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")

        val theirSliver = armOtherSeat(driver, opponent)

        withClue("the enabler sits with the granter's controller, so the gate is open for everyone") {
            castOffered(driver, opponent, theirSliver) shouldBe true
        }
    }

    test("an enabler on the caster's side does not open someone else's gate") {
        val driver = createDriver()
        val me = openingPosition(driver)
        val opponent = driver.getOpponent(me)
        driver.putPermanentOnBattlefield(me, "Test Shared Flash Granter")
        // The only enabler is on the *caster's* battlefield. Evaluating the condition in the
        // caster's context would wrongly open the gate; the granter's controller has none.
        driver.putPermanentOnBattlefield(opponent, "Test Flash Enabler")

        val theirSliver = armOtherSeat(driver, opponent)

        withClue("'you control' in the granter's ability means its controller, not the caster") {
            castOffered(driver, opponent, theirSliver) shouldBe false
        }
    }

    test("a stolen granter grants to its new controller, not to its owner") {
        val driver = createDriver()
        val me = driver.activePlayer!!
        val opponent = driver.getOpponent(me)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putPermanentOnBattlefield(me, "Test Conditional Flash Granter")
        // An enabler on each side, so the gate's condition holds in either player's context and
        // the only thing these assertions can be measuring is who the engine thinks controls the
        // granter — the battlefield scan reads the ownership-keyed zone map if it is wrong.
        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")
        driver.putPermanentOnBattlefield(opponent, "Test Flash Enabler")
        val granter = driver.findPermanent(me, "Test Conditional Flash Granter")!!

        driver.passPriority(me)
        val stealCard = driver.putCardInHand(opponent, "Test Steal")
        driver.giveMana(opponent, Color.BLUE, 2)
        driver.castSpell(opponent, stealCard, listOf(granter)).isSuccess shouldBe true
        // Pass exactly as far as it takes to resolve the steal — `bothPass()` would pass once more
        // afterwards. `PassPriorityHandler.resolveTopOfStack` hands priority back to the resolved
        // spell's caster, so one more pass is needed to get back to my seat.
        while (driver.state.stack.isNotEmpty()) {
            driver.passPriority(driver.state.priorityPlayerId!!)
        }
        withClue("the steal resolved") {
            driver.state.projectedState.getController(granter) shouldBe opponent
        }
        if (driver.state.priorityPlayerId != me) {
            driver.passPriority(driver.state.priorityPlayerId!!)
        }
        driver.state.priorityPlayerId shouldBe me

        // Rebuild the sorcery-illegal board now that the stack has emptied again.
        val bolt = driver.putCardInHand(me, "Lightning Bolt")
        driver.giveMana(me, Color.RED, 2)
        driver.castSpell(me, bolt, listOf(opponent)).isSuccess shouldBe true
        driver.state.stack.isEmpty() shouldBe false
        val mySliver = driver.putCardInHand(me, "Test Gated Sliver")

        withClue("the granter is no longer mine, so its controllerOnly grant is no longer mine") {
            castOffered(driver, me, mySliver) shouldBe false
        }

        val theirSliver = armOtherSeat(driver, opponent)
        withClue("it belongs to the thief now, enabler and all") {
            castOffered(driver, opponent, theirSliver) shouldBe true
        }
    }

    test("a phased-out granter grants nothing (CR 702.26b)") {
        val driver = createDriver()
        val me = openingPosition(driver)
        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")
        val sliver = driver.putCardInHand(me, "Test Gated Sliver")

        castOffered(driver, me, sliver) shouldBe true

        // The shape PhaseOutExecutor produces: the permanent stays in its zone, marked phased out.
        val granter = driver.findPermanent(me, "Test Conditional Flash Granter")!!
        driver.replaceState(
            driver.state.updateEntity(granter) { it.with(PhasedOutComponent(me)) }
        )

        withClue("a phased-out permanent is treated as though it does not exist") {
            castOffered(driver, me, sliver) shouldBe false
        }
    }

    test("the permission goes away with the granter even while the condition still holds") {
        val driver = createDriver()
        val me = openingPosition(driver)
        driver.putPermanentOnBattlefield(me, "Test Flash Enabler")
        val sliver = driver.putCardInHand(me, "Test Gated Sliver")

        castOffered(driver, me, sliver) shouldBe true

        val granter = driver.findPermanent(me, "Test Conditional Flash Granter")!!
        driver.moveToGraveyard(granter)

        withClue("the enabler is still there, but nothing is granting flash any more") {
            castOffered(driver, me, sliver) shouldBe false
        }
    }
})
