package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.state.components.identity.FaceDownModeComponent
import com.wingedsheep.engine.state.ZoneKey
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.mkm.cards.CrypticCoat
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.FaceDownMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Cryptic Coat (MKM #50) — {2}{U} Artifact — Equipment.
 *
 * "When this Equipment enters, cloak the top card of your library, then attach this Equipment to
 * it. Equipped creature gets +1/+0 and can't be blocked. {1}{U}: Return this Equipment to its
 * owner's hand."
 *
 * The whole card hangs on the "then": the Equipment must land on the very permanent its own
 * trigger just created, with no targeting. That is a two-step pipeline handoff —
 * `MoveCollectionEffect.storeMovedAs` republishes the *battlefield* entity id after the face-down
 * move, and `EffectTarget.PipelineTarget` feeds it to the attach. If that handoff silently
 * resolved to nothing, the card would still cloak and would still look correct on the board, so
 * the attachment assertion is the point of this file, not decoration.
 *
 * Cryptic Coat has no equip ability by design (Scryfall ruling 2024-02-02), so the bounce test
 * doubles as the "re-cloak" loop check: return it, recast it, and a second card is cloaked.
 */
class CrypticCoatScenarioTest : FunSpec({

    val bear = card("Coat Test Bear") {
        manaCost = "{1}{G}"
        typeLine = "Creature — Bear"
        power = 2
        toughness = 2
    }

    val bounceAbilityId = CrypticCoat.activatedAbilities.single().id

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(CrypticCoat, bear))
        driver.initMirrorMatch(deck = Deck.of("Island" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    fun GameTestDriver.faceDownPermanents(playerId: EntityId) =
        getPermanents(playerId).filter { state.getEntity(it)?.has<FaceDownComponent>() == true }

    /** Cast the Coat from hand and let its enters trigger resolve. */
    fun GameTestDriver.playCoat(playerId: EntityId): EntityId {
        val inHand = putCardInHand(playerId, "Cryptic Coat")
        giveMana(playerId, Color.BLUE, 3)
        castSpell(playerId, inHand).error shouldBe null
        bothPass() // the Coat resolves; its enters trigger goes on the stack
        bothPass() // the trigger resolves
        return inHand
    }

    test("the enters trigger cloaks the top card and attaches the Coat to it") {
        val driver = newDriver()
        val player = driver.player1

        val topCard = driver.putCardOnTopOfLibrary(player, "Coat Test Bear")
        val librarySizeBefore = driver.state.getLibrary(player).size

        val coat = driver.playCoat(player)

        // The top card — that exact card — is now a face-down cloaked permanent.
        val cloaked = driver.faceDownPermanents(player)
        cloaked shouldBe listOf(topCard)
        driver.state.getEntity(topCard)?.get<FaceDownModeComponent>()?.mode shouldBe FaceDownMode.CLOAK
        driver.state.getLibrary(player).size shouldBe librarySizeBefore - 1

        // …and the Coat is attached to it. This is the pipeline handoff under test.
        val attachment = driver.state.getEntity(coat)?.get<AttachedToComponent>()
        attachment.shouldNotBeNull()
        attachment.targetId shouldBe topCard
    }

    test("the equipped cloaked creature is a 3/2 that can't be blocked") {
        val driver = newDriver()
        val player = driver.player1

        val topCard = driver.putCardOnTopOfLibrary(player, "Coat Test Bear")
        driver.playCoat(player)

        val projected = driver.state.projectedState
        // 2/2 face-down body (CR 701.58a) plus the Equipment's +1/+0.
        projected.getPower(topCard) shouldBe 3
        projected.getToughness(topCard) shouldBe 2
        // Ward {2} is a face-down characteristic of cloak, kept alongside the Coat's grants.
        projected.hasKeyword(topCard, Keyword.WARD) shouldBe true
        projected.hasKeyword(topCard, AbilityFlag.CANT_BE_BLOCKED) shouldBe true
    }

    test("{1}{U} returns the Coat to hand, unattaching it") {
        val driver = newDriver()
        val player = driver.player1

        val topCard = driver.putCardOnTopOfLibrary(player, "Coat Test Bear")
        val coat = driver.playCoat(player)

        driver.giveMana(player, Color.BLUE, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = coat, abilityId = bounceAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.state.getZone(ZoneKey(player, Zone.HAND)).contains(coat) shouldBe true
        driver.state.getEntity(coat)?.get<AttachedToComponent>() shouldBe null
        // The cloaked creature stays behind as a plain 2/2 — the buff left with the Coat.
        driver.state.projectedState.getPower(topCard) shouldBe 2
        driver.state.projectedState.hasKeyword(topCard, AbilityFlag.CANT_BE_BLOCKED) shouldBe false
    }

    test("recasting the returned Coat cloaks a second card") {
        val driver = newDriver()
        val player = driver.player1

        driver.putCardOnTopOfLibrary(player, "Coat Test Bear")
        val coat = driver.playCoat(player)

        driver.giveMana(player, Color.BLUE, 2)
        driver.submit(
            ActivateAbility(playerId = player, sourceId = coat, abilityId = bounceAbilityId)
        ).isSuccess shouldBe true
        driver.bothPass()

        driver.putCardOnTopOfLibrary(player, "Coat Test Bear")
        driver.giveMana(player, Color.BLUE, 3)
        driver.castSpell(player, coat).error shouldBe null
        driver.bothPass()
        driver.bothPass()

        driver.faceDownPermanents(player).size shouldBe 2
        driver.state.getEntity(coat)?.get<AttachedToComponent>().shouldNotBeNull()
    }
})
