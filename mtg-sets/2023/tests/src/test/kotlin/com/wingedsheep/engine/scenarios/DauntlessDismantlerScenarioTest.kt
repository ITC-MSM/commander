package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.core.ChooseNumberDecision
import com.wingedsheep.engine.core.NumberChosenResponse
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.DauntlessDismantler
import com.wingedsheep.mtg.sets.tokens.PredefinedTokens
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Dauntless Dismantler (LCI) — {1}{W} Creature — Human Artificer 1/4
 *
 * "Artifacts your opponents control enter tapped."
 * "{X}{X}{W}, Sacrifice this creature: Destroy each artifact with mana value X."
 *
 * Tests:
 * 1. [PermanentsEnterTapped] replacement fires for opponents' artifacts — they enter tapped.
 * 2. Replacement does NOT fire for the controller's own artifacts (opponent-scoped filter).
 * 3. Activating with X=N destroys only artifacts with mana value N; Dismantler is sacrificed;
 *    artifacts with a different mana value survive.
 */
class DauntlessDismantlerScenarioTest : FunSpec({

    // A {0} creature whose ETB creates a predefined Map artifact token (noncreature artifact),
    // exercising the CreatePredefinedTokenExecutor path — the exact Waterwind Scout repro.
    val mapMaker = card("Map Maker (test)") {
        manaCost = "{0}"
        typeLine = "Creature — Scout"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.CreateMapToken(1)
        }
    }

    // A {0} creature whose ETB creates a 1/1 Artifact Creature — Construct token, exercising the
    // general CreateTokenExecutor path.
    val constructMaker = card("Construct Maker (test)") {
        manaCost = "{0}"
        typeLine = "Creature — Scout"
        power = 1
        toughness = 1
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.CreateToken(
                power = 1, toughness = 1, creatureTypes = setOf("Construct"), artifactToken = true,
            )
        }
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(DauntlessDismantler, mapMaker, constructMaker))
        // CreatePredefinedTokenExecutor looks up the Map token definition by name.
        d.registerCards(PredefinedTokens.allTokens)
        return d
    }

    // -------------------------------------------------------------------------
    // Replacement effect — artifacts opponents control enter tapped
    // -------------------------------------------------------------------------

    /**
     * Player 2 is the starting/active player. Player 1 (non-active) controls Dismantler.
     * When player 2 (an opponent of player 1) casts Sol Ring during their main phase,
     * the enters-tapped replacement fires and Sol Ring arrives tapped.
     */
    test("opponent's artifact enters tapped while Dismantler controls the battlefield") {
        // startingPlayer = 1 makes player2 the active player so they can cast sorcery-speed
        // permanents during their own main phase while player1's Dismantler is already in play.
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = d.activePlayer!!            // player2 — will cast the artifact
        val dismantlerOwner = d.getOpponent(active) // player1 — controls Dismantler

        // Dismantler on player1's battlefield (replacement effect now active).
        d.putCreatureOnBattlefield(dismantlerOwner, "Dauntless Dismantler")

        // Player2 casts Sol Ring ({1}, MV 1) from hand — they are an opponent of player1.
        val solRingId = d.putCardInHand(active, "Sol Ring")
        d.giveColorlessMana(active, 1)
        d.castSpell(active, solRingId).isSuccess shouldBe true
        // Resolve the stack (Sol Ring resolves; no further triggered abilities expected).
        repeat(10) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        // Sol Ring entered under player2 (opponent of Dismantler's controller) → tapped.
        val solRing = d.findPermanent(active, "Sol Ring")!!
        d.isTapped(solRing) shouldBe true
    }

    /**
     * The replacement is opponent-scoped: the controller's own artifacts enter untapped.
     */
    test("controller's own artifact does NOT enter tapped (replacement is opponent-scoped)") {
        val d = driver()
        // Default startingPlayer=0: player1 is active and can cast sorcery-speed spells.
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = d.activePlayer!!  // player1

        d.putCreatureOnBattlefield(active, "Dauntless Dismantler")

        // Player1 (Dismantler's controller) casts their own Sol Ring.
        val solRingId = d.putCardInHand(active, "Sol Ring")
        d.giveColorlessMana(active, 1)
        d.castSpell(active, solRingId).isSuccess shouldBe true
        repeat(10) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        // Sol Ring belongs to Dismantler's controller → NOT tapped.
        val solRing = d.findPermanent(active, "Sol Ring")!!
        d.isTapped(solRing) shouldBe false
    }

    // -------------------------------------------------------------------------
    // Activated ability — {X}{X}{W}, Sacrifice: Destroy each artifact with MV X
    // -------------------------------------------------------------------------

    /**
     * Activating with X=1 destroys all artifacts with mana value 1 (Sol Ring) and leaves
     * artifacts with a different mana value (Triskelion, MV 6) untouched. The Dismantler
     * is sacrificed as part of paying the cost (before the effect resolves).
     */
    test("activating with X=1 destroys MV-1 artifacts only; Dismantler is sacrificed") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = d.activePlayer!!
        val opp = d.getOpponent(active)

        val dismantler = d.putCreatureOnBattlefield(active, "Dauntless Dismantler")

        // Set up targets on opponent's battlefield.
        // Sol Ring ({1}, MV 1) — will be destroyed when X=1.
        d.putPermanentOnBattlefield(opp, "Sol Ring")
        // Triskelion ({6}, MV 6) — should survive when X=1.
        d.putCreatureOnBattlefield(opp, "Triskelion")

        // {X}{X}{W} with X=1 costs {1}{1}{W} = 2 generic + 1 white = 3 mana total.
        d.giveMana(active, Color.WHITE, 1)
        d.giveColorlessMana(active, 2)

        val abilityId = DauntlessDismantler.activatedAbilities.first().id
        // Bare activation (no pre-filled xValue) — engine pauses to ask for X.
        val res = d.submit(ActivateAbility(playerId = active, sourceId = dismantler, abilityId = abilityId))
        res.isPaused shouldBe true
        d.pendingDecision.shouldBeInstanceOf<ChooseNumberDecision>()

        // Choose X = 1.
        d.submitDecision(active, NumberChosenResponse(d.pendingDecision!!.id, 1))
        // Drain remaining decisions (mana payment, effect resolution, etc.).
        repeat(15) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        // Dismantler was sacrificed as cost — no longer on battlefield.
        d.findPermanent(active, "Dauntless Dismantler") shouldBe null

        // Sol Ring (MV 1) was destroyed.
        d.findPermanent(opp, "Sol Ring") shouldBe null

        // Triskelion (MV 6) was NOT destroyed — it survives X=1.
        d.findPermanent(opp, "Triskelion") shouldNotBe null
    }

    // -------------------------------------------------------------------------
    // Replacement applies to TOKENS too — "Artifacts your opponents control enter tapped"
    // taps artifact tokens an opponent creates, not only cast/played artifacts.
    // -------------------------------------------------------------------------

    /**
     * The reported bug: an opponent's Map token (created by Waterwind Scout in play) should enter
     * tapped while player1's Dauntless Dismantler is on the battlefield. Predefined-token path
     * (CreatePredefinedTokenExecutor).
     */
    test("opponent's created Map artifact token enters tapped (replacement applies to tokens)") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = d.activePlayer!!               // player2 — will create the token
        val dismantlerOwner = d.getOpponent(active) // player1 — controls Dismantler

        d.putCreatureOnBattlefield(dismantlerOwner, "Dauntless Dismantler")

        // Player2 casts the {0} maker; its ETB creates a Map token under player2 (an opponent of
        // Dismantler's controller).
        val makerId = d.putCardInHand(active, "Map Maker (test)")
        d.castSpell(active, makerId).isSuccess shouldBe true
        repeat(12) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        val map = d.findPermanent(active, "Map")!!
        d.isTapped(map) shouldBe true
    }

    /**
     * The general CreateTokenExecutor path: an opponent's artifact-creature token also enters
     * tapped under Dauntless Dismantler.
     */
    test("opponent's created artifact-creature token enters tapped (general token path)") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true, startingPlayer = 1)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = d.activePlayer!!
        val dismantlerOwner = d.getOpponent(active)

        d.putCreatureOnBattlefield(dismantlerOwner, "Dauntless Dismantler")

        val makerId = d.putCardInHand(active, "Construct Maker (test)")
        d.castSpell(active, makerId).isSuccess shouldBe true
        repeat(12) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        val construct = d.findPermanent(active, "Construct Token")!!
        d.isTapped(construct) shouldBe true
    }

    /**
     * Opponent-scoped: the Dismantler controller's OWN artifact token enters untapped — the
     * replacement must not over-apply.
     */
    test("controller's own created artifact token does NOT enter tapped (opponent-scoped)") {
        val d = driver()
        d.initMirrorMatch(deck = Deck.of("Plains" to 40), skipMulligans = true)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        val active = d.activePlayer!!  // player1 — controls Dismantler and makes the token

        d.putCreatureOnBattlefield(active, "Dauntless Dismantler")

        val makerId = d.putCardInHand(active, "Map Maker (test)")
        d.castSpell(active, makerId).isSuccess shouldBe true
        repeat(12) { if (d.pendingDecision != null) d.autoResolveDecision() else d.bothPass() }

        val map = d.findPermanent(active, "Map")!!
        d.isTapped(map) shouldBe false
    }
})
