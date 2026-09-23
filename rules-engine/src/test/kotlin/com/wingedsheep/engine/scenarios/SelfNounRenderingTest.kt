package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.stack.ActivatedAbilityOnStackComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.engine.view.ClientStateTransformer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.model.EntityId
import com.wingedsheep.sdk.scripting.effects.TransformEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import com.wingedsheep.engine.core.Outcome

/**
 * Type-aware self-noun rendering. A [TransformEffect]/`Grant*` effect targeting
 * [EffectTarget.Self] carries the self-noun placeholder token in its `descriptionTemplate`; the
 * `ClientStateTransformer` substitutes the noun for the *source permanent's actual (projected)
 * type* when it renders the ability on the stack — "this creature" on a creature, "this artifact"
 * on an artifact — instead of the type-neutral "this permanent" default. Both cards below carry the
 * identical `TransformEffect(Self)` ability, so only the source's type drives the rendered noun.
 */
class SelfNounRenderingTest : FunSpec({

    val beast = card("Self Transform Beast") {
        manaCost = "{1}"
        typeLine = "Creature — Beast"
        power = 2
        toughness = 2
        activatedAbility {
            cost = Costs.Tap
            effect = TransformEffect(EffectTarget.Self)
        }
    }
    val relic = card("Self Transform Relic") {
        manaCost = "{1}"
        typeLine = "Artifact"
        activatedAbility {
            cost = Costs.Tap
            effect = TransformEffect(EffectTarget.Self)
        }
    }

    fun newDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all + listOf(beast, relic))
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40), skipMulligans = true, startingPlayer = 0)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)
        return driver
    }

    // Activate the source's (only) {T} ability and return the rendered oracle text of the resulting
    // ability object on the stack, as the active player's client would see it.
    fun stackAbilityText(driver: GameTestDriver, player: EntityId, source: EntityId): String {
        val abilityId = driver.state.getEntity(source)!!
            .get<com.wingedsheep.engine.state.components.identity.CardComponent>()!!
            .let { driver.cardRegistry.requireCard(it.cardDefinitionId) }
            .activatedAbilities.first().id
        driver.submit(ActivateAbility(playerId = player, sourceId = source, abilityId = abilityId))
            .outcome shouldBe Outcome.Done
        val stackId = driver.state.stack.first {
            driver.state.getEntity(it)?.has<ActivatedAbilityOnStackComponent>() == true
        }
        return ClientStateTransformer(driver.cardRegistry)
            .transform(driver.state, player).cards[stackId]!!.oracleText
    }

    test("a creature's self-referential Transform ability renders 'this creature' on the stack") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        val source = driver.putCreatureOnBattlefield(player, "Self Transform Beast")
        driver.removeSummoningSickness(source)

        val text = stackAbilityText(driver, player, source)
        text shouldContain "this creature"
        text shouldNotContain "this permanent"
    }

    test("an artifact's self-referential Transform ability renders 'this artifact', never 'this creature'") {
        val driver = newDriver()
        val player = driver.activePlayer!!
        val source = driver.putPermanentOnBattlefield(player, "Self Transform Relic")

        val text = stackAbilityText(driver, player, source)
        text shouldContain "this artifact"
        text shouldNotContain "this creature"
    }
})
