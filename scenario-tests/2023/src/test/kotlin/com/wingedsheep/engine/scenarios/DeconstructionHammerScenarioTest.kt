package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.state.components.battlefield.AttachedToComponent
import com.wingedsheep.engine.state.components.stack.ChosenTarget
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lci.cards.DeconstructionHammer
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import com.wingedsheep.sdk.scripting.GrantActivatedAbility
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

/**
 * Deconstruction Hammer (LCI #9) — {W} Artifact — Equipment.
 *
 * "Equipped creature gets +1/+1 and has '{3}, {T}, Sacrifice Deconstruction Hammer: Destroy
 *  target artifact or enchantment.'"
 *
 * Focus: the granted ability's "Sacrifice Deconstruction Hammer" cost. Per CR 201.5a the name
 * refers only to the specific granting Equipment, so it is modeled as
 * [com.wingedsheep.sdk.dsl.Costs.SacrificeGrantingPermanent]. This test pins that the cost
 * sacrifices exactly the *attached* Hammer — not a second same-named Hammer elsewhere on the
 * battlefield — with no target prompt for the sacrifice.
 */
class DeconstructionHammerScenarioTest : FunSpec({

    val grantedAbilityId = DeconstructionHammer.staticAbilities
        .filterIsInstance<GrantActivatedAbility>().first().ability.id
    val equipAbilityId = DeconstructionHammer.activatedAbilities.single { it.isEquipAbility }.id

    fun setup(): GameTestDriver = GameTestDriver().apply {
        registerCards(TestCards.all)
        initMirrorMatch(deck = Deck.of("Plains" to 40), startingLife = 20, skipMulligans = true)
    }

    test("granted ability sacrifices the attached Hammer (not a second same-named one) and destroys the target") {
        val d = setup()
        val p1 = d.activePlayer!!
        val opponent = d.getOpponent(p1)

        val bear = d.putCreatureOnBattlefield(p1, "Grizzly Bears")
        val attachedHammer = d.putPermanentOnBattlefield(p1, "Deconstruction Hammer")
        // A second, unattached Deconstruction Hammer that must NOT be sacrificed.
        val otherHammer = d.putPermanentOnBattlefield(p1, "Deconstruction Hammer")
        val victim = d.putPermanentOnBattlefield(opponent, "Ornithopter") // 0/2 artifact creature

        d.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // Equip the first Hammer onto the Bear.
        d.giveColorlessMana(p1, 1)
        d.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = attachedHammer,
                abilityId = equipAbilityId,
                targets = listOf(ChosenTarget.Permanent(bear))
            )
        ).isSuccess shouldBe true
        d.bothPass()
        d.state.getEntity(attachedHammer)?.get<AttachedToComponent>()?.targetId shouldBe bear

        // Activate the granted "{3}, {T}, Sacrifice Deconstruction Hammer: Destroy target
        // artifact or enchantment" on the equipped creature, destroying the Ornithopter.
        d.removeSummoningSickness(bear)
        d.giveColorlessMana(p1, 3)
        d.submit(
            ActivateAbility(
                playerId = p1,
                sourceId = bear,
                abilityId = grantedAbilityId,
                targets = listOf(ChosenTarget.Permanent(victim))
            )
        ).isSuccess shouldBe true
        var guard = 0
        while (d.state.stack.isNotEmpty() && d.pendingDecision == null && guard++ < 20) d.bothPass()

        // The *attached* Hammer was sacrificed; the second Hammer is untouched.
        d.getGraveyard(p1) shouldContain attachedHammer
        d.getGraveyard(p1) shouldNotContain otherHammer
        d.state.getEntity(otherHammer)?.get<AttachedToComponent>() shouldBe null
        d.findPermanent(p1, "Deconstruction Hammer") shouldBe otherHammer
        // The target artifact was destroyed.
        d.findPermanent(opponent, "Ornithopter") shouldBe null
    }
})
