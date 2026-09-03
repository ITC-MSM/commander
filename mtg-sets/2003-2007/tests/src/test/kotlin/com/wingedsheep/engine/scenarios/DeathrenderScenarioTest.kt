package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.battlefield.AttachmentsComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lrw.cards.Deathrender
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Deathrender (LRW #255) — "Equipped creature gets +2/+2. Whenever equipped creature dies, you may
 * put a creature card from your hand onto the battlefield and attach this Equipment to it.
 * Equip {2}"
 *
 * The load-bearing claim is that the Equipment lands on the creature it just cheated in. Two
 * halves have to hold at once for that: the dies trigger must fire at all (an
 * [AttachmentsComponent] link that is already severed by the state-based unattach is the known
 * hazard on this shape), and the attach must name the card *after* it has moved from hand to
 * battlefield — which is what `PipelineTarget("putting", 0)` buys.
 *
 * The declining case is the other half of the printed "you may": nothing enters, and the
 * Equipment must not attach to something else or crash on an empty collection.
 */
class DeathrenderScenarioTest : FunSpec({

    /**
     * Put the Equipment onto the battlefield already attached to [host] — the Forebear's Blade
     * idiom. Cheaper and less brittle than paying an equip activation, and the equip cost is not
     * what this card is about.
     */
    fun GameTestDriver.putEquipmentAttached(
        playerId: EntityId,
        cardName: String,
        host: EntityId
    ): EntityId {
        val equipmentId = putPermanentOnBattlefield(playerId, cardName)
        var newState = state.updateEntity(equipmentId) { c -> c.with(AttachedToComponent(host)) }
        val existing = newState.getEntity(host)?.get<AttachmentsComponent>()?.attachedIds ?: emptyList()
        newState = newState.updateEntity(host) { c -> c.with(AttachmentsComponent(existing + equipmentId)) }
        replaceState(newState)
        return equipmentId
    }

    fun driver(): GameTestDriver {
        val d = GameTestDriver()
        d.registerCards(TestCards.all + listOf(Deathrender))
        d.initMirrorMatch(deck = Deck.of("Swamp" to 40), startingPlayer = 0)
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return d
    }

    test("equipped creature gets +2/+2") {
        val d = driver()
        val me = d.activePlayer!!
        val host = d.putCreatureOnBattlefield(me, "Centaur Courser")
        d.putEquipmentAttached(me, "Deathrender", host)

        d.state.projectedState.getPower(host) shouldBe 5
        d.state.projectedState.getToughness(host) shouldBe 5
    }

    test("when the host dies, a creature card from hand enters and Deathrender moves onto it") {
        val d = driver()
        val me = d.activePlayer!!
        val host = d.putCreatureOnBattlefield(me, "Savannah Lions")
        val render = d.putEquipmentAttached(me, "Deathrender", host)
        val force = d.putCardInHand(me, "Force of Nature")

        d.giveMana(me, Color.BLACK, 2)
        val doomBlade = d.putCardInHand(me, "Doom Blade")
        d.castSpellWithTargets(me, doomBlade, listOf(ChosenTarget.Permanent(host)))
        d.bothPass()

        withClue("the host is gone") { d.getPermanents(me).contains(host) shouldBe false }

        // Resolve the Equipment's dies trigger; the pipeline then asks which card to put in.
        d.bothPass()
        d.pendingDecision.shouldNotBeNull()
        d.submitCardSelection(me, listOf(force)).isSuccess shouldBe true

        val inPlay = d.findPermanent(me, "Force of Nature").shouldNotBeNull()
        withClue("the card was put onto the battlefield, not cast") { inPlay shouldBe force }
        withClue("\"and attach this Equipment to it\"") {
            d.state.getEntity(render)?.get<AttachedToComponent>()?.targetId shouldBe inPlay
        }
        withClue("the new host wears the +2/+2") {
            d.state.projectedState.getPower(inPlay) shouldBe 7
            d.state.projectedState.getToughness(inPlay) shouldBe 7
        }
    }

    test("declining the may leaves the card in hand and Deathrender unattached") {
        val d = driver()
        val me = d.activePlayer!!
        val host = d.putCreatureOnBattlefield(me, "Savannah Lions")
        val render = d.putEquipmentAttached(me, "Deathrender", host)
        val force = d.putCardInHand(me, "Force of Nature")

        d.giveMana(me, Color.BLACK, 2)
        val doomBlade = d.putCardInHand(me, "Doom Blade")
        d.castSpellWithTargets(me, doomBlade, listOf(ChosenTarget.Permanent(host)))
        d.bothPass()
        d.bothPass()

        d.pendingDecision.shouldNotBeNull()
        d.submitCardSelection(me, emptyList()).isSuccess shouldBe true

        d.findPermanent(me, "Force of Nature") shouldBe null
        d.getHand(me) shouldContain force
        withClue("nothing entered, so the Equipment has nothing to move onto") {
            d.state.getEntity(render)?.get<AttachedToComponent>()?.targetId shouldBe null
        }
        withClue("the Equipment itself is still on the battlefield") {
            d.getPermanents(me) shouldContain render
        }
    }
})
