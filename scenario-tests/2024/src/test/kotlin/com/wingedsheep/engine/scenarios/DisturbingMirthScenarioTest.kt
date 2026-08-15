package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.CardsSelectedResponse
import com.wingedsheep.engine.core.SelectCardsDecision
import com.wingedsheep.engine.core.YesNoDecision
import com.wingedsheep.engine.core.YesNoResponse
import com.wingedsheep.engine.handlers.continuations.entityIdToChosenTarget
import com.wingedsheep.engine.state.components.identity.FaceDownComponent
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetPermanent
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Disturbing Mirth (DSK #212) — {B}{R} Enchantment.
 *
 *  - When this enchantment enters, you may sacrifice another enchantment or creature. If you do,
 *    draw two cards.
 *  - When you sacrifice this enchantment, manifest dread.
 *
 * The ETB is the standard optional-sacrifice reflexive flow (Boilerbilges Ripper shape) with an
 * untargeted "draw two" payoff. The sacrifice-self trigger reuses the shared manifest-dread recipe.
 */
class DisturbingMirthScenarioTest : FunSpec({

    // Inline sac outlet so we can sacrifice Disturbing Mirth itself and observe its sacrifice trigger.
    val sacOutlet = card("Sac Outlet") {
        manaCost = "{1}"
        typeLine = "Sorcery"
        spell {
            target("target permanent you control", TargetPermanent(filter = TargetFilter.PermanentYouControl))
            effect = Effects.SacrificeTarget(EffectTarget.ContextTarget(0))
        }
    }

    fun driver(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all + sacOutlet)
        initMirrorMatch(deck = Deck.of("Forest" to 40), startingLife = 20)
    }

    test("ETB: sacrificing another creature draws two cards") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val fodder = d.putCreatureOnBattlefield(you, "Grizzly Bears")
        val handBefore = d.getHand(you).size

        val mirth = d.putCardInHand(you, "Disturbing Mirth")
        d.giveMana(you, Color.BLACK, 1)
        d.giveMana(you, Color.RED, 1)
        d.castSpell(you, mirth)
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // "You may sacrifice another enchantment or creature." — accept, then pick the fodder.
        (d.pendingDecision as? YesNoDecision)?.let {
            d.submitDecision(you, YesNoResponse(decisionId = it.id, choice = true))
        }
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()
        (d.pendingDecision as? SelectCardsDecision)?.let {
            d.submitDecision(you, CardsSelectedResponse(decisionId = it.id, selectedCards = listOf(fodder)))
        }
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        // The fodder was sacrificed and we drew two cards.
        d.getPermanents(you).contains(fodder) shouldBe false
        d.getHand(you).size shouldBe handBefore + 2
    }

    test("sacrificing Disturbing Mirth itself manifests dread") {
        val d = driver()
        val you = d.activePlayer!!
        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Put Disturbing Mirth onto the battlefield directly (skip its ETB for this case).
        val mirth = d.putPermanentOnBattlefield(you, "Disturbing Mirth")

        // Library top two for the manifest-dread look.
        d.putCardOnTopOfLibrary(you, "Forest")
        val creature = d.putCardOnTopOfLibrary(you, "Centaur Courser")

        // Cast the sac outlet targeting Disturbing Mirth — sacrificing it triggers manifest dread.
        val outlet = d.putCardInHand(you, "Sac Outlet")
        d.giveColorlessMana(you, 1)
        d.castSpellWithTargets(you, outlet, listOf(entityIdToChosenTarget(d.state, mirth)))
        while (!d.isPaused && d.state.stack.isNotEmpty()) d.bothPass()

        d.getPermanents(you).contains(mirth) shouldBe false

        // Manifest dread pauses on the pick.
        val pick = d.pendingDecision.shouldBeInstanceOf<SelectCardsDecision>()
        d.submitDecision(you, CardsSelectedResponse(decisionId = pick.id, selectedCards = listOf(creature)))

        d.getPermanents(you).contains(creature) shouldBe true
        d.state.getEntity(creature)?.get<FaceDownComponent>() shouldBe FaceDownComponent
    }
})
