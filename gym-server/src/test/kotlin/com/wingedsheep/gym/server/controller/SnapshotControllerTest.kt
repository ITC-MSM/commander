package com.wingedsheep.gym.server.controller

import com.wingedsheep.engine.registry.CardRegistry
import com.wingedsheep.engine.state.GameState
import com.wingedsheep.gym.service.MultiEnvService
import com.wingedsheep.gym.service.SnapshotCodec
import com.wingedsheep.gym.service.SnapshotHandle
import com.wingedsheep.gym.server.config.WebConfig
import kotlinx.serialization.encodeToString
import org.springframework.http.MediaType
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SnapshotControllerTest : FunSpec({
    test("HTTP snapshot disposal round trips handles and retains unrelated snapshots") {
        val codec = SnapshotCodec()
        val controller = SnapshotController(MultiEnvService(CardRegistry(), snapshotCodec = codec))
        val json = WebConfig().gymJson()
        val mvc = MockMvcBuilders.standaloneSetup(controller)
            .setMessageConverters(KotlinSerializationJsonHttpMessageConverter(json))
            .build()
        val first: SnapshotHandle = codec.save(GameState(), emptyList(), 0)
        val second: SnapshotHandle = codec.save(GameState(), emptyList(), 0)
        val retained = codec.save(GameState(), emptyList(), 7)

        mvc.perform(delete("/snapshots")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.encodeToString(first)))
            .andReturn().response.status shouldBe 204
        codec.size() shouldBe 2
        mvc.perform(delete("/snapshots/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json.encodeToString(listOf(first, second, second))))
            .andReturn().response.status shouldBe 204
        codec.size() shouldBe 1
        codec.load(retained).stepCount shouldBe 7
    }

    test("dispose releases the supplied snapshot handle") {
        val codec = SnapshotCodec()
        val service = MultiEnvService(CardRegistry(), snapshotCodec = codec)
        val handle = codec.save(GameState(), emptyList(), 0)
        val controller = SnapshotController(service)

        controller.dispose(handle).statusCode.value() shouldBe 204
        codec.size() shouldBe 0
    }

    test("disposeBatch releases all supplied handles and remains idempotent") {
        val codec = SnapshotCodec()
        val service = MultiEnvService(CardRegistry(), snapshotCodec = codec)
        val first = codec.save(GameState(), emptyList(), 0)
        val second = codec.save(GameState(), emptyList(), 0)
        val controller = SnapshotController(service)

        controller.disposeBatch(listOf(first, second, first)).statusCode.value() shouldBe 204
        codec.size() shouldBe 0

        controller.disposeBatch(listOf(first, second)).statusCode.value() shouldBe 204
        controller.disposeBatch(emptyList()).statusCode.value() shouldBe 204
        codec.size() shouldBe 0
    }
})
