package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.handlers.effects.ZoneMovementUtils
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.GrantMayPlayFromExileEffect
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.references.Player
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/**
 * Tests for [MayPlayExpiry.WhileYouControlSource] — the "you may cast it for as long as you
 * control this [permanent]" window on a cast-from-exile permission (Taster of Wares).
 *
 * The `MayPlayExpiry` mirror of [Duration.WhileYouControlSource], and it must behave the same way:
 *
 *  - The window closes when the granting permanent leaves the battlefield **or** when its
 *    *projected* controller stops being the grant's controller — a Threaten-style steal of the
 *    source ends it just as a destroy does.
 *  - One-way per CR 611.2b. [com.wingedsheep.engine.mechanics.sba.permanent.EndedDurationExpiryCheck]
 *    physically removes the permission, so regaining control of the source does not revive it.
 *  - A window that is already over when the granting ability resolves never opens at all — the
 *    rule's own Master Thief example. No permission is created.
 *
 * Contrast [MayPlayExpiry.Permanent], which deliberately survives the source leaving play; the
 * last test pins that difference so the two can't silently converge.
 */
class MayPlayWhileYouControlSourceTest : FunSpec({

    val projector = StateProjector()

    // "{T}: Exile the top card of your library. You may play it for as long as you control this."
    val SourceBoundExiler = card("Source Bound Exiler") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 2
        toughness = 2
        oracleText = "{T}: Exile the top card of your library. You may play that card for as long " +
            "as you control this creature."
        activatedAbility {
            cost = Costs.Tap
            effect = Patterns.Exile.impulse(1, MayPlayExpiry.WhileYouControlSource("this creature"))
        }
    }

    // Same shape, but the permission outlives the source. The control test for the above.
    val PermanentExiler = card("Permanent Exiler") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 2
        toughness = 2
        oracleText = "{T}: Exile the top card of your library. You may play that card for as long " +
            "as it remains exiled."
        activatedAbility {
            cost = Costs.Tap
            effect = Patterns.Exile.impulse(1, MayPlayExpiry.Permanent)
        }
    }

    // "{T}: Exile each card from an opponent's hand. For each of those cards, its OWNER may
    // play it for as long as you control this creature." The `ownerControls` grouping keys the
    // permission to each card's owner, while "you" in the duration stays the activating player —
    // the one grouping where those two diverge.
    val OwnerControlsExiler = card("Owner Controls Exiler") {
        manaCost = "{0}"
        typeLine = "Creature — Test"
        power = 2
        toughness = 2
        oracleText = "{T}: Exile each card from an opponent's hand. For each of those cards, its " +
            "owner may play it for as long as you control this creature."
        activatedAbility {
            cost = Costs.Tap
            effect = Effects.Composite(
                listOf(
                    GatherCardsEffect(
                        source = CardSource.FromZone(Zone.HAND, Player.AnOpponent),
                        storeAs = "theirs"
                    ),
                    MoveCollectionEffect(
                        from = "theirs",
                        destination = CardDestination.ToZone(Zone.EXILE, Player.AnOpponent)
                    ),
                    GrantMayPlayFromExileEffect(
                        from = "theirs",
                        expiry = MayPlayExpiry.WhileYouControlSource("this creature"),
                        ownerControls = true
                    )
                )
            )
        }
    }

    val DestroyAnyPermanent = card("Destroy Any Permanent") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Destroy target permanent."
        spell {
            val t = target("permanent", Targets.Permanent)
            effect = Effects.Destroy(t)
        }
    }

    val ThreatenAnyPermanent = card("Threaten Any Permanent") {
        manaCost = "{0}"
        typeLine = "Sorcery"
        oracleText = "Gain control of target permanent until end of turn."
        spell {
            val t = target("permanent", Targets.Permanent)
            effect = Effects.GainControl(t, Duration.EndOfTurn)
        }
    }

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(
            TestCards.all + listOf(
                SourceBoundExiler, PermanentExiler, ThreatenAnyPermanent, DestroyAnyPermanent,
                OwnerControlsExiler
            )
        )
        return driver
    }

    /** Put [cardName] on the battlefield for [owner], ready, and activate its exile ability. */
    fun GameTestDriver.activateExilerFor(owner: EntityId, cardName: String): EntityId {
        val definition = if (cardName == "Source Bound Exiler") SourceBoundExiler else PermanentExiler
        val exiler = putCreatureOnBattlefield(owner, cardName)
        removeSummoningSickness(exiler)
        val result = submit(
            ActivateAbility(
                playerId = owner,
                sourceId = exiler,
                abilityId = definition.activatedAbilities.first().id
            )
        )
        result.isSuccess shouldBe true
        bothPass()
        return exiler
    }

    test("the permission is granted and persists while you control the source") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.activateExilerFor(active, "Source Bound Exiler")

        driver.state.mayPlayPermissions.size shouldBe 1
        driver.state.mayPlayPermissions.single().endsWhenSourceUncontrolled shouldBe true
        driver.state.mayPlayPermissions.single().controllerId shouldBe active

        // Not turn-keyed: it must survive cleanup across several turn cycles.
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 600)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 600)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 600)

        driver.state.mayPlayPermissions.size shouldBe 1
    }

    test("the permission is revoked when the source leaves the battlefield") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val exiler = driver.activateExilerFor(active, "Source Bound Exiler")
        driver.state.mayPlayPermissions.size shouldBe 1

        // Destroy it for real, so the engine's own state-based action pass is what revokes the
        // permission — the path a game actually takes.
        val destroy = driver.putCardInHand(active, "Destroy Any Permanent")
        driver.castSpell(active, destroy, listOf(exiler))
        driver.bothPass()
        driver.state.getBattlefield().contains(exiler) shouldBe false

        driver.state.mayPlayPermissions.isEmpty() shouldBe true
    }

    test("a MayPlayExpiry.Permanent grant survives the same source leaving — the contrast case") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val exiler = driver.activateExilerFor(active, "Permanent Exiler")
        driver.state.mayPlayPermissions.size shouldBe 1
        driver.state.mayPlayPermissions.single().endsWhenSourceUncontrolled shouldBe false

        val destroy = driver.putCardInHand(active, "Destroy Any Permanent")
        driver.castSpell(active, destroy, listOf(exiler))
        driver.bothPass()
        driver.state.getBattlefield().contains(exiler) shouldBe false

        driver.state.mayPlayPermissions.size shouldBe 1
    }

    test("the permission is revoked when an opponent steals the source (Threaten-style)") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val exiler = driver.activateExilerFor(active, "Source Bound Exiler")
        driver.state.mayPlayPermissions.size shouldBe 1

        // Hand the turn to the opponent and let them steal the exiler itself. The source is still
        // on the battlefield — only its controller changed — and that alone must end the window.
        driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        if (driver.activePlayer != opponent) driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        driver.activePlayer shouldBe opponent
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)

        val threaten = driver.putCardInHand(opponent, "Threaten Any Permanent")
        driver.castSpell(opponent, threaten, listOf(exiler))
        driver.bothPass()

        projector.project(driver.state).getController(exiler) shouldBe opponent
        driver.state.mayPlayPermissions.isEmpty() shouldBe true
    }

    test("CR 611.2b — regaining control of the source does NOT revive the permission") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val exiler = driver.activateExilerFor(active, "Source Bound Exiler")

        driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        if (driver.activePlayer != opponent) driver.passPriorityUntil(Step.DRAW, maxPasses = 200)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN, maxPasses = 200)
        val threaten = driver.putCardInHand(opponent, "Threaten Any Permanent")
        driver.castSpell(opponent, threaten, listOf(exiler))
        driver.bothPass()
        driver.state.mayPlayPermissions.isEmpty() shouldBe true

        // The Threaten wears off at cleanup and the exiler comes back under its original
        // controller — but a window that has closed stays closed.
        driver.passPriorityUntil(Step.DRAW, maxPasses = 600)
        if (driver.activePlayer != active) driver.passPriorityUntil(Step.DRAW, maxPasses = 600)
        driver.activePlayer shouldBe active
        projector.project(driver.state).getController(exiler) shouldBe active
        driver.state.mayPlayPermissions.isEmpty() shouldBe true
    }

    test("an ownerControls grant keys the window to the granting player, not each card's owner") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        val opponent = driver.getOpponent(active)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        driver.putCardInHand(opponent, "Destroy Any Permanent")
        val exiler = driver.putCreatureOnBattlefield(active, "Owner Controls Exiler")
        driver.removeSummoningSickness(exiler)
        driver.submit(
            ActivateAbility(
                playerId = active,
                sourceId = exiler,
                abilityId = OwnerControlsExiler.activatedAbilities.first().id
            )
        ).isSuccess shouldBe true
        driver.bothPass()

        // The permission belongs to the OWNER of the exiled card (the opponent), but the "for as
        // long as you control this creature" window belongs to the player who activated it. If the
        // window keyed off `controllerId` here it would resolve to the opponent — who never
        // controls the source — and the very next state-based check would delete the grant.
        val permission = driver.state.mayPlayPermissions.single()
        permission.controllerId shouldBe opponent
        permission.endsWhenSourceUncontrolled shouldBe true
        permission.expiryControllerId shouldBe active

        // Surviving the state-based check that `bothPass` just ran is itself the regression: with
        // the window keyed off `controllerId`, that pass would already have deleted the grant.

        // …and it still ends when the source goes, exactly like the controller-scoped grant.
        val destroy = driver.putCardInHand(active, "Destroy Any Permanent")
        driver.castSpell(active, destroy, listOf(exiler))
        driver.bothPass()
        driver.state.getBattlefield().contains(exiler) shouldBe false
        driver.state.mayPlayPermissions.isEmpty() shouldBe true
    }

    test("CR 611.2b — no permission at all when the source is gone before the ability resolves") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
        val active = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val exiler = driver.putCreatureOnBattlefield(active, "Source Bound Exiler")
        driver.removeSummoningSickness(exiler)
        driver.submit(
            ActivateAbility(
                playerId = active,
                sourceId = exiler,
                abilityId = SourceBoundExiler.activatedAbilities.first().id
            )
        ).isSuccess shouldBe true

        // Kill the source with the ability still on the stack. The ability resolves (CR 608.2) and
        // still exiles the card, but its "for as long as you control this creature" duration is
        // already over, so the grant never happens — the Master Thief case in CR 611.2b.
        val moved = ZoneMovementUtils.moveCardToZone(driver.state, exiler, Zone.GRAVEYARD)
        driver.replaceState(moved.state)
        driver.bothPass()

        driver.state.getExile(active).size shouldBe 1
        driver.state.mayPlayPermissions.isEmpty() shouldBe true
    }
})
