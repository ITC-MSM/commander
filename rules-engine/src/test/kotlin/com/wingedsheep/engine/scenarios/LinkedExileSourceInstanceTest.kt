package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.OrderObjectsDecision
import com.wingedsheep.engine.core.OrderedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardSource
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

/** The linked-exile slice needed by champion, independently of its sacrifice and champion event. */
class LinkedExileSourceInstanceTest : FunSpec({
    val probe = card("Linked Exile Probe") {
        manaCost = "{2}{U}"
        typeLine = "Creature — Shapeshifter"
        power = 3
        toughness = 3
        triggeredAbility {
            trigger = Triggers.EntersBattlefield
            effect = Effects.Pipeline {
                val eligible = gather(CardSource.BattlefieldMatching(
                    GameObjectFilter.Creature.youControl().notSourceItself()
                ))
                val selected = chooseUpTo(1, eligible, useTargetingUI = true)
                move(selected, CardDestination.ToZone(Zone.EXILE), linkToSource = true)
            }
        }
        triggeredAbility {
            trigger = Triggers.LeavesBattlefield
            effect = Effects.ReturnLinkedExileUnderOwnersControl()
        }
    }
    val blink = card("Linked Exile Blink") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.Exile(creature).then(Effects.PutOntoBattlefield(creature))
        }
    }
    val banish = card("Linked Exile Banish") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val creature = target("creature", Targets.Creature)
            effect = Effects.Exile(creature)
        }
    }

    val rescue = card("Linked Exile Rescue") {
        manaCost = "{U}"
        typeLine = "Instant"
        spell {
            val exiled = target("exiled", com.wingedsheep.sdk.scripting.targets.TargetObject(filter = Targets.Unified.inExile()))
            effect = Effects.PutOntoBattlefield(exiled)
        }
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + listOf(probe, blink, banish, rescue))
        initMirrorMatch(deck = Deck.of("Island" to 40), startingPlayer = 0)
        passPriorityUntil(Step.PRECOMBAT_MAIN)
    }

    fun GameTestDriver.resolveChoosing(selected: EntityId? = null) {
        var guard = 0
        while (stackSize > 0 || pendingDecision != null) {
            check(guard++ < 16) { "Resolution did not settle" }
            when (val decision = pendingDecision) {
                null -> bothPass().error shouldBe null
                is SelectCardsDecision -> submitCardSelection(decision.playerId,
                    selected?.let { listOf(it) } ?: emptyList()).error shouldBe null
                is OrderObjectsDecision -> submitDecision(decision.playerId,
                    OrderedResponse(decision.id, decision.objects)).error shouldBe null
                else -> error("Unexpected decision: $decision")
            }
        }
    }

    for (returnSource in listOf(false, true)) {
        test("leaves trigger returns its linked creature when source returns=$returnSource") {
            val d = driver()
            val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            val source = d.putCardInHand(d.player1, "Linked Exile Probe")
            d.giveMana(d.player1, Color.BLUE, 3)
            d.castSpell(d.player1, source).error shouldBe null
            d.resolveChoosing(victim)
            d.state.getBattlefield().contains(victim) shouldBe false

            val removal = d.putCardInHand(d.player1,
                if (returnSource) "Linked Exile Blink" else "Linked Exile Banish")
            d.giveMana(d.player1, Color.BLUE, 1)
            d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
            // Decline the new instance's optional exile; the old instance still owes its return.
            d.resolveChoosing()

            d.state.getBattlefield().contains(victim) shouldBe true
            d.state.getBattlefield().contains(source) shouldBe returnSource
        }
    }

    test("old and returned source visits have separate exile piles") {
        val d = driver()
        val first = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val second = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val source = d.putCardInHand(d.player1, "Linked Exile Probe")
        d.giveMana(d.player1, Color.BLUE, 3)
        d.castSpell(d.player1, source).error shouldBe null
        d.resolveChoosing(first)

        val flicker = d.putCardInHand(d.player1, "Linked Exile Blink")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.castSpell(d.player1, flicker, listOf(source)).error shouldBe null
        d.resolveChoosing(second)
        (first in d.state.getBattlefield()) shouldBe true
        (second in d.state.getBattlefield()) shouldBe false

        val removal = d.putCardInHand(d.player1, "Linked Exile Banish")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing()
        (first in d.state.getBattlefield()) shouldBe true
        (second in d.state.getBattlefield()) shouldBe true
        d.state.departedLinkedExile shouldBe emptyMap()
    }

    test("an entry trigger can exile after its source and return trigger have left the stack") {
        val d = driver()
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val source = d.putCardInHand(d.player1, "Linked Exile Probe")
        d.giveMana(d.player1, Color.BLUE, 3)
        d.castSpell(d.player1, source).error shouldBe null
        d.bothPass().error shouldBe null
        d.stackSize shouldBe 1

        val removal = d.putCardInHand(d.player1, "Linked Exile Banish")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.resolveChoosing(victim)
        (source in d.state.getBattlefield()) shouldBe false
        (victim in d.state.getBattlefield()) shouldBe false
        d.state.departedLinkedExile.values.flatten() shouldBe listOf(victim)
    }


    test("a vanished token source still returns its linked card") {
        val d = driver()
        val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
        val source = d.putCardInHand(d.player1, "Linked Exile Probe")
        d.giveMana(d.player1, Color.BLUE, 3)
        d.castSpell(d.player1, source).error shouldBe null
        d.resolveChoosing(victim)
        d.replaceState(d.state.updateEntity(source) {
            it.with(com.wingedsheep.engine.state.components.identity.TokenComponent)
        })
        val removal = d.putCardInHand(d.player1, "Linked Exile Banish")
        d.giveMana(d.player1, Color.BLUE, 1)
        d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
        d.bothPass().error shouldBe null
        d.stackSize shouldBe 1
        val json = kotlinx.serialization.json.Json {
            serializersModule = com.wingedsheep.engine.core.engineSerializersModule
            allowStructuredMapKeys = true
        }
        val serializer = com.wingedsheep.engine.state.GameState.serializer()
        d.replaceState(json.decodeFromString(serializer, json.encodeToString(serializer, d.state)))
        d.resolveChoosing()
        d.state.getEntity(source) shouldBe null
        (victim in d.state.getBattlefield()) shouldBe true
        d.state.departedLinkedExile shouldBe emptyMap()
    }


    for (departed in listOf(false, true)) {
        test("leaving and reentering exile does not restore a link with departed source=$departed") {
            val d = driver()
            val victim = d.putCreatureOnBattlefield(d.player1, "Grizzly Bears")
            val source = d.putCardInHand(d.player1, "Linked Exile Probe")
            d.giveMana(d.player1, Color.BLUE, 3)
            d.castSpell(d.player1, source).error shouldBe null
            d.resolveChoosing(victim)

            if (departed) {
                val removal = d.putCardInHand(d.player1, "Linked Exile Banish")
                d.giveMana(d.player1, Color.BLUE, 1)
                d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
                d.bothPass().error shouldBe null
                d.stackSize shouldBe 1
            }
            val returnCard = d.putCardInHand(d.player1, "Linked Exile Rescue")
            d.giveMana(d.player1, Color.BLUE, 1)
            d.castSpellWithTargets(d.player1, returnCard, listOf(
                com.wingedsheep.engine.state.components.stack.ChosenTarget.Card(victim, d.player1, Zone.EXILE)
            )).error shouldBe null
            d.bothPass().error shouldBe null
            (victim in d.state.getBattlefield()) shouldBe true

            val banishAgain = d.putCardInHand(d.player1, "Linked Exile Banish")
            d.giveMana(d.player1, Color.BLUE, 1)
            d.castSpell(d.player1, banishAgain, listOf(victim)).error shouldBe null
            d.bothPass().error shouldBe null
            if (!departed) {
                val removal = d.putCardInHand(d.player1, "Linked Exile Banish")
                d.giveMana(d.player1, Color.BLUE, 1)
                d.castSpell(d.player1, removal, listOf(source)).error shouldBe null
            }
            d.resolveChoosing()
            (victim in d.state.getBattlefield()) shouldBe false
            d.state.departedLinkedExile shouldBe emptyMap()
        }
    }

})
