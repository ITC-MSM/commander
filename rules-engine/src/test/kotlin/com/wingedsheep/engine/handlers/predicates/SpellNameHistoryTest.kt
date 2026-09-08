package com.wingedsheep.engine.handlers.predicates

import com.wingedsheep.engine.state.CastSpellRecord
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.sdk.core.TypeLine
import com.wingedsheep.sdk.model.EntityId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SpellNameHistoryTest : FunSpec({
    fun history(name: String?, faceDown: Boolean = false) = GameState(
        spellsCastThisTurnByPlayer = mapOf(EntityId("caster") to listOf(
            CastSpellRecord(TypeLine.parse("Instant"), 1, emptySet(), faceDown, name = name)
        ))
    )

    test("matches recorded names without requiring the spell to remain in a zone") {
        sharesNameWithSpellCastThisTurn(history("Lightning Bolt"), "Lightning Bolt") shouldBe true
        sharesNameWithSpellCastThisTurn(history("Lightning Bolt"), "Shock") shouldBe false
    }
    test("nameless and face-down spells never establish a matching name") {
        sharesNameWithSpellCastThisTurn(history(null), null) shouldBe false
        sharesNameWithSpellCastThisTurn(history(""), "") shouldBe false
        sharesNameWithSpellCastThisTurn(history("Lightning Bolt", true), "Lightning Bolt") shouldBe false
    }
    test("multiple names match if at least one is shared") {
        sharesNameWithSpellCastThisTurn(history("Fire"), "Fire // Ice") shouldBe true
        sharesNameWithSpellCastThisTurn(history("Fire // Ice"), "Ice") shouldBe true
        sharesNameWithSpellCastThisTurn(history("Fire"), "Ice") shouldBe false
    }
    test("clearing turn history removes eligibility") {
        val state = history("Lightning Bolt")
        sharesNameWithSpellCastThisTurn(state.copy(spellsCastThisTurnByPlayer = emptyMap()), "Lightning Bolt") shouldBe false
    }
})
