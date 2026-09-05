package com.wingedsheep.sdk.serialization

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.scripting.KeywordAbility
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class DredgeSerializationTest : StringSpec({
    "dredge retains its number and display keyword through serialization" {
        for (amount in listOf(0, 1, 3, 6)) {
            val original = KeywordAbility.dredge(amount)
            val restored = CardSerialization.json.decodeFromString(
                KeywordAbility.serializer(),
                CardSerialization.json.encodeToString(KeywordAbility.serializer(), original)
            )
            restored shouldBe original
            restored.description shouldBe "Dredge $amount"
            restored.keyword shouldBe Keyword.DREDGE
        }
    }
    "negative dredge is rejected" {
        shouldThrow<IllegalArgumentException> { KeywordAbility.dredge(-1) }
    }
})
