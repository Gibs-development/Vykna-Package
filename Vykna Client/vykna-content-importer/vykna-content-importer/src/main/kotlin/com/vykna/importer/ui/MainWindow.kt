package com.vykna.importer.ui

import com.vykna.importer.AppController
import com.vykna.importer.core.Log
import com.vykna.importer.preview.GzipUtil
import com.vykna.importer.preview.RsModel
import com.vykna.importer.preview.RsModelDecoder
import com.vykna.importer.preview.RsModelMerger
import com.vykna.importer.preview.WireframeRenderer
import com.vykna.importer.preview.anim.FrameGroup
import com.vykna.importer.preview.anim.FrameGroupDecoder
import com.vykna.importer.preview.anim.RsAnimator
import com.vykna.importer.source.defs.ItemLite
import com.vykna.importer.source.defs.NpcLite
import com.vykna.importer.source.defs.SpotAnimLite
import javafx.animation.AnimationTimer
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.stage.DirectoryChooser
import javafx.stage.Stage
import java.io.File
import java.util.prefs.Preferences
import kotlin.math.max

/**
 * Viewer-first tool:
 * - NPC tab shows merged in-world model (all body models).
 * - Seq tab previews sequences on a chosen model (defaults to current NPC model).
 * - SpotAnims tab stays a static model preview for now.
 *
 * Export remains intentionally disabled.
 */
class MainWindow {

    private val prefs = Preferences.userRoot().node("vykna-content-importer")

    // ------------ global preview state (shared) ------------
    private var yawDeg = 45.0
    private var pitchDeg = 20.0
    private var zoom = 1.0
    private var flipY = true
    private var swapYZ = false
    private var includeChatModels = false // placeholder checkbox (NpcLite doesn't decode them yet)

    // ------------ cache + status ------------
    private val baseCacheField = TextField()
    private val sourceCacheField = TextField()
    private val status = Label("Ready.")
    private var loaded: AppController.Loaded? = null

    // ------------ tabs ------------
    private lateinit var tabs: TabPane
    private var seqTabIndex: Int = 3

    // ------------ NPC tab ------------
    private val npcSearch = TextField()
    private val npcList = ListView<String>()
    private val npcDetails = TextArea()
    private val npcCanvas = Canvas(520.0, 420.0)
    private var npcIndex: List<NpcLite> = emptyList()
    private var npcFiltered: List<NpcLite> = emptyList()

    private var lastNpc: NpcLite? = null
    private var lastNpcMergedModel: RsModel? = null

    private val npcStandBtn = Button("Stand")
    private val npcWalkBtn = Button("Walk")
    private val npcTurn180Btn = Button("Turn180")
    private val npcTurn90RBtn = Button("Turn90R")
    private val npcWalkBackBtn = Button("WalkBack")
    private val npcWalkLeftBtn = Button("WalkLeft")
    private val npcWalkRightBtn = Button("WalkRight")
    private val npcSeqField = TextField()
    private val npcSeqPlayBtn = Button("Play")

    // ------------ Items tab ------------
    private val itemSearch = TextField()
    private val itemList = ListView<String>()
    private val itemDetails = TextArea()
    private val itemCanvas = Canvas(520.0, 420.0)
    private var itemIndex: List<ItemLite> = emptyList()
    private var itemFiltered: List<ItemLite> = emptyList()

    private var lastItem: ItemLite? = null
    private var lastItemModel: RsModel? = null

    private val itemUseInSeqBtn = Button("Use in anim preview")
    private val itemSeqField = TextField()
    private val itemSeqPlayBtn = Button("Play")

    // ------------ SpotAnim tab ------------
    private val gfxSearch = TextField()
    private val gfxList = ListView<String>()
    private val gfxDetails = TextArea()
    private val gfxCanvas = Canvas(520.0, 420.0)
    private var gfxIndex: List<SpotAnimLite> = emptyList()
    private var gfxFiltered: List<SpotAnimLite> = emptyList()
    private var lastGfxModel: RsModel? = null

    // ------------ Sequences tab ------------
    private val seqSearch = TextField()
    private val seqList = ListView<String>()
    private val seqDetails = TextArea()
    private val seqCanvas = Canvas(520.0, 420.0)
    private var seqIndex: List<Int> = emptyList()
    private var seqFiltered: List<Int> = emptyList()

    private val seqUseNpcModelBtn = Button("Use current NPC model")
    private val seqModelIdsField = TextField()
    private val seqLoadModelBtn = Button("Load model(s)")

    private val seqPlayPauseBtn = Button("Play")
    private val seqStopBtn = Button("Stop")
    private val seqLoopChk = CheckBox("Loop").apply { isSelected = true }
    private val seqSpeed = Slider(0.25, 4.0, 1.0).apply { isShowTickLabels = true; isShowTickMarks = true }
    private val seqFrameLbl = Label("Frame: -")

    private val seqPlayer = SeqPlayer()

    fun start(stage: Stage) {
        stage.title = "Vykna Content Importer (667 → Cache)"

        baseCacheField.text = prefs.get("baseCache", "C:\\Users\\Cal\\.runerogue_v1")
        sourceCacheField.text = prefs.get("source667", "C:\\Users\\Cal\\667dump")

        val root = BorderPane().apply { padding = Insets(10.0) }

        root.top = VBox(8.0).apply {
            children += cachePickRow(
                title = "Base cache (your Vykna cache folder)",
                field = baseCacheField,
                onPick = { pickDir(stage)?.let { baseCacheField.text = it.absolutePath } }
            )
            children += cachePickRow(
                title = "Source cache (667 dump folder)",
                field = sourceCacheField,
                onPick = { pickDir(stage)?.let { sourceCacheField.text = it.absolutePath } }
            )

            val loadBtn = Button("Load caches")
            loadBtn.setOnAction {
                try {
                    val basePath = baseCacheField.text.trim()
                    val srcPath = sourceCacheField.text.trim()

                    prefs.put("baseCache", basePath)
                    prefs.put("source667", srcPath)

                    val baseDir = File(basePath)
                    val srcDir = File(srcPath)
                    require(baseDir.exists() && baseDir.isDirectory) { "Base cache folder not found: $basePath" }
                    require(srcDir.exists() && srcDir.isDirectory) { "Source 667 folder not found: $srcPath" }

                    status.text = "Loading 667 defs... (see console)"
                    Log.info("Loading source 667 from $srcPath")

                    val ctrl = AppController()
                    loaded = ctrl.loadSource667(srcDir)

                    npcIndex = loaded!!.npcs
                    itemIndex = loaded!!.items
                    gfxIndex = loaded!!.spotanims
                    seqIndex = loaded!!.seqs.keys.sorted()

                    applyNpcFilter()
                    applyItemFilter()
                    applyGfxFilter()
                    applySeqFilter()

                    status.text = "Loaded: NPCs=${npcIndex.size}, SpotAnims=${gfxIndex.size}, Items=${loaded!!.items.size}, Seqs=${loaded!!.seqs.size}"
                } catch (t: Throwable) {
                    status.text = "Error: ${t.message}"
                    t.printStackTrace()
                }
            }
            children += loadBtn
        }

        tabs = TabPane().apply {
            tabs.add(tabNpcs())
            tabs.add(tabItems())
            tabs.add(tabSpotAnims())
            tabs.add(tabSequences())
        }
        root.center = tabs
        root.bottom = status

        val scene = Scene(root, 1100.0, 700.0)
        scene.stylesheets.add(javaClass.getResource("/dark.css")!!.toExternalForm())
        stage.scene = scene
        stage.show()

        stage.setOnCloseRequest { seqPlayer.stop() }
    }

    // ---------------- NPC TAB ----------------

    private fun tabNpcs(): Tab {
        val t = Tab("NPCs").apply { isClosable = false }

        npcSearch.promptText = "Search by name or id (e.g. glacor / 6650)"
        npcSearch.textProperty().addListener { _, _, _ -> applyNpcFilter() }

        npcList.selectionModel.selectedIndexProperty().addListener { _, _, new ->
            val idx = new?.toInt() ?: -1
            if (idx < 0 || idx >= npcFiltered.size) return@addListener
            showNpc(npcFiltered[idx])
        }

        val flipYChk = CheckBox("Flip Y").apply {
            isSelected = flipY
            setOnAction { flipY = isSelected; rerenderNpc(); rerenderSeq() }
        }
        val swapYZChk = CheckBox("Swap Y/Z").apply {
            isSelected = swapYZ
            setOnAction { swapYZ = isSelected; rerenderNpc(); rerenderSeq() }
        }
        val includeChatChk = CheckBox("Include chat models").apply {
            isSelected = includeChatModels
            tooltip = Tooltip("(Placeholder) Head/chat models (opcode 60). We'll wire decoding later.")
            setOnAction {
                includeChatModels = isSelected
                // NpcLite currently doesn't expose chat models; checkbox is here so the UI won't change later.
                showNpc(lastNpc ?: return@setOnAction)
            }
        }
        val resetBtn = Button("Reset view").apply {
            setOnAction {
                yawDeg = 45.0
                pitchDeg = 20.0
                zoom = 1.0
                rerenderNpc(); rerenderSeq(); rerenderGfx()
            }
        }
        val controls = HBox(10.0, flipYChk, swapYZChk, includeChatChk, resetBtn).apply { alignment = Pos.CENTER_LEFT }

        val npcCanvasWrap = resizableCanvas(npcCanvas) { rerenderNpc() }
        installCanvasControls(npcCanvas) { rerenderNpc() }

        npcDetails.isEditable = false
        npcDetails.prefHeight = 200.0
        VBox.setVgrow(npcDetails, Priority.NEVER)

        npcSeqField.promptText = "Seq id…"
        npcSeqField.prefWidth = 100.0

        val animButtons = FlowPane(8.0, 8.0).apply {
            prefWrapLength = 520.0
            alignment = Pos.CENTER_LEFT
            children.addAll(
                npcStandBtn,
                npcWalkBtn,
                npcTurn180Btn,
                npcTurn90RBtn,
                npcWalkBackBtn,
                npcWalkLeftBtn,
                npcWalkRightBtn
            )
        }

        val animRow = VBox(6.0,
            HBox(8.0, Label("Animate:"), npcSeqField, npcSeqPlayBtn).apply { alignment = Pos.CENTER_LEFT },
            animButtons
        )

        listOf(
            npcStandBtn, npcWalkBtn, npcTurn180Btn, npcTurn90RBtn,
            npcWalkBackBtn, npcWalkLeftBtn, npcWalkRightBtn
        ).forEach { it.isDisable = true }
        npcSeqPlayBtn.setOnAction {
            val seqId = npcSeqField.text.trim().toIntOrNull() ?: return@setOnAction
            val m = lastNpcMergedModel
            if (m != null) {
                seqPlayer.setModel(m, "NPC ${lastNpc?.id ?: "?"} ${lastNpc?.name ?: ""}")
            }
            openSeqAndPlay(seqId)
        }

        val pane = BorderPane().apply { padding = Insets(10.0) }
        pane.top = npcSearch
        pane.left = npcList.apply { prefWidth = 420.0 }
        pane.center = VBox(
            8.0,
            Label("Preview"),
            controls,
            npcCanvasWrap,
            animRow,
            Label("Details"),
            npcDetails
        ).apply { padding = Insets(10.0) }

        t.content = pane
        return t
    }

    private fun applyNpcFilter() {
        val q = npcSearch.text.trim().lowercase()
        npcFiltered = if (q.isEmpty()) npcIndex else npcIndex.filter { it.name.lowercase().contains(q) || it.id.toString() == q }
        npcList.items.setAll(npcFiltered.take(5000).map { "${it.id} - ${cleanText(it.name)}" })
    }

    private fun showNpc(n: NpcLite) {
        loaded ?: return
        lastNpc = n

        // buttons jump to sequences tab + play on NPC model
        setupSeqBtn(npcStandBtn, "Stand", n.standAnim)
        setupSeqBtn(npcWalkBtn, "Walk", n.walkAnim)
        setupSeqBtn(npcTurn180Btn, "Turn180", n.turn180Anim)
        setupSeqBtn(npcTurn90RBtn, "Turn90R", n.turn90RightAnim)
        setupSeqBtn(npcWalkBackBtn, "WalkBack", n.walkBackAnim)
        setupSeqBtn(npcWalkLeftBtn, "WalkLeft", n.walkLeftAnim)
        setupSeqBtn(npcWalkRightBtn, "WalkRight", n.walkRightAnim)

        // Merge ALL body models for preview (this was previously only the first model).
        val modelIds = n.models
        val merged = buildMergedModel(modelIds)
        lastNpcMergedModel = merged
        rerenderNpc()

        npcDetails.text = buildString {
            appendLine("NPC ${n.id}: ${n.name}")
            if (!n.examine.isNullOrBlank()) appendLine("Examine: ${cleanText(n.examine)}")
            appendLine("Combat: ${n.combatLevel}")
            appendLine("Size: ${n.size}")
            if (n.childrenIDs.isNotEmpty()) appendLine("ChildrenIDs: ${n.childrenIDs.joinToString(", ")}")
            appendLine()

            val acts = n.actions.mapIndexedNotNull { i, a ->
                val v = a?.let { cleanText(it).trim() }
                if (v.isNullOrEmpty()) null else "$i=\"$v\""
            }
            appendLine("Actions: ${if (acts.isEmpty()) "[]" else acts.joinToString(", ")}")
            appendLine()

            appendLine("Models (${n.models.size}): ${n.models.joinToString(", ")}")
            appendLine("StandSeq:     ${n.standAnim}")
            appendLine("WalkSeq:      ${n.walkAnim}")
            appendLine("Turn180Seq:   ${n.turn180Anim}")
            appendLine("Turn90RSeq:   ${n.turn90RightAnim}")
            appendLine("WalkBackSeq:  ${n.walkBackAnim}")
            appendLine("WalkLeftSeq:  ${n.walkLeftAnim}")
            appendLine("WalkRightSeq: ${n.walkRightAnim}")
            appendLine()
            appendLine("--- Pasteable snippet ---")
            appendLine(npcDefinitionSnippet(n))
        }
    }

    private fun setupSeqBtn(btn: Button, label: String, seqId: Int) {
        btn.text = if (seqId >= 0) "$label: $seqId" else "$label: -"
        btn.isDisable = seqId < 0
        btn.setOnAction {
            if (seqId < 0) return@setOnAction
            // Force the NPC model for NPC-anim buttons, even if the Sequences tab was previously animating something else.
            val m = lastNpcMergedModel
            if (m != null) {
                seqPlayer.setModel(m, "NPC ${lastNpc?.id ?: "?"} ${lastNpc?.name ?: ""}")
            }
            openSeqAndPlay(seqId)
        }
    }

    private fun rerenderNpc() {
        val model = lastNpcMergedModel
        if (model == null) {
            clearCanvas(npcCanvas)
            return
        }
        WireframeRenderer.render(npcCanvas, model, yawDeg, pitchDeg, zoom, flipY, swapYZ)
    }

    // ---------------- ITEMS TAB ----------------

    private fun tabItems(): Tab {
        val t = Tab("Items").apply { isClosable = false }

        itemSearch.promptText = "Search by name, id or model id (e.g. rune / 4151 / 12345)"
        itemSearch.textProperty().addListener { _, _, _ -> applyItemFilter() }

        itemList.selectionModel.selectedIndexProperty().addListener { _, _, new ->
            val idx = new?.toInt() ?: -1
            if (idx < 0 || idx >= itemFiltered.size) return@addListener
            showItem(itemFiltered[idx])
        }

        val flipYChk = CheckBox("Flip Y").apply {
            isSelected = flipY
            setOnAction { flipY = isSelected; rerenderItem(); rerenderSeq() }
        }
        val swapYZChk = CheckBox("Swap Y/Z").apply {
            isSelected = swapYZ
            setOnAction { swapYZ = isSelected; rerenderItem(); rerenderSeq() }
        }
        val resetBtn = Button("Reset view").apply {
            setOnAction {
                yawDeg = 45.0
                pitchDeg = 20.0
                zoom = 1.0
                rerenderItem(); rerenderSeq()
            }
        }
        val controls = HBox(10.0, flipYChk, swapYZChk, resetBtn).apply { alignment = Pos.CENTER_LEFT }

        val itemCanvasWrap = resizableCanvas(itemCanvas) { rerenderItem() }
        installCanvasControls(itemCanvas) { rerenderItem() }

        itemDetails.isEditable = false
        itemDetails.prefHeight = 200.0
        VBox.setVgrow(itemDetails, Priority.NEVER)

        itemSeqField.promptText = "Seq id…"
        itemSeqField.prefWidth = 100.0

        itemUseInSeqBtn.isDisable = true
        itemSeqPlayBtn.isDisable = true

        itemUseInSeqBtn.setOnAction {
            val m = lastItemModel
            if (m == null) {
                status.text = "Pick an item first."
                return@setOnAction
            }
            val it = lastItem
            seqPlayer.setModel(m, "Item ${it?.id ?: "?"} ${it?.name ?: ""}")
            tabs.selectionModel.select(seqTabIndex)
            rerenderSeq()
        }

        itemSeqPlayBtn.setOnAction {
            val seqId = itemSeqField.text.trim().toIntOrNull() ?: return@setOnAction
            val m = lastItemModel
            if (m == null) {
                status.text = "Pick an item first."
                return@setOnAction
            }
            val it = lastItem
            seqPlayer.setModel(m, "Item ${it?.id ?: "?"} ${it?.name ?: ""}")
            openSeqAndPlay(seqId)
        }

        val animRow = HBox(8.0, itemUseInSeqBtn, Label("Seq:"), itemSeqField, itemSeqPlayBtn).apply {
            alignment = Pos.CENTER_LEFT
        }

        val pane = BorderPane().apply { padding = Insets(10.0) }
        pane.top = itemSearch
        pane.left = itemList.apply { prefWidth = 420.0 }
        pane.center = VBox(
            8.0,
            Label("Preview"),
            controls,
            itemCanvasWrap,
            animRow,
            Label("Details"),
            itemDetails
        ).apply { padding = Insets(10.0) }

        t.content = pane
        return t
    }

    private fun applyItemFilter() {
        val q = itemSearch.text.trim().lowercase()
        itemFiltered = if (q.isEmpty()) itemIndex else itemIndex.filter {
            it.name.lowercase().contains(q) || it.id.toString() == q || it.model.toString() == q
        }
        itemList.items.setAll(itemFiltered.take(5000).map { "${it.id} - ${cleanText(it.name)}  (model=${it.model})" })
    }

    private fun showItem(i: ItemLite) {
        loaded ?: return
        lastItem = i

        lastItemModel = buildMergedModel(intArrayOf(i.model))
        rerenderItem()

        itemUseInSeqBtn.isDisable = lastItemModel == null
        itemSeqPlayBtn.isDisable = lastItemModel == null

        val li = loaded!!
        val modelExists = li.fileIndex.modelCandidates.containsKey(i.model) || li.fileIndex.models.containsKey(i.model)

        itemDetails.text = buildString {
            appendLine("Item ${i.id}: ${cleanText(i.name)}")
            appendLine("Model: ${i.model} (exists=$modelExists)")
            appendLine()
            appendLine("Tip: Click 'Use in anim preview' then use the Sequences tab to browse/play animations.")
        }
    }

    private fun rerenderItem() {
        val model = lastItemModel
        if (model == null) {
            clearCanvas(itemCanvas)
            return
        }
        WireframeRenderer.render(itemCanvas, model, yawDeg, pitchDeg, zoom, flipY, swapYZ)
    }

    // ---------------- SPOTANIMS TAB (static preview) ----------------

    private fun tabSpotAnims(): Tab {
        val t = Tab("SpotAnims (GFX)").apply { isClosable = false }

        gfxSearch.promptText = "Search by id or model/seq id"
        gfxSearch.textProperty().addListener { _, _, _ -> applyGfxFilter() }

        gfxList.selectionModel.selectedIndexProperty().addListener { _, _, new ->
            val idx = new?.toInt() ?: -1
            if (idx < 0 || idx >= gfxFiltered.size) return@addListener
            showGfx(gfxFiltered[idx])
        }

        val flipYChk = CheckBox("Flip Y").apply {
            isSelected = flipY
            setOnAction { flipY = isSelected; rerenderGfx() }
        }
        val swapYZChk = CheckBox("Swap Y/Z").apply {
            isSelected = swapYZ
            setOnAction { swapYZ = isSelected; rerenderGfx() }
        }
        val resetBtn = Button("Reset view").apply {
            setOnAction {
                yawDeg = 45.0
                pitchDeg = 20.0
                zoom = 1.0
                rerenderGfx()
            }
        }
        val controls = HBox(10.0, flipYChk, swapYZChk, resetBtn).apply { alignment = Pos.CENTER_LEFT }

        val gfxCanvasWrap = resizableCanvas(gfxCanvas) { rerenderGfx() }
        installCanvasControls(gfxCanvas) { rerenderGfx() }

        gfxDetails.isEditable = false
        gfxDetails.prefHeight = 200.0
        VBox.setVgrow(gfxDetails, Priority.NEVER)

        val pane = BorderPane().apply { padding = Insets(10.0) }
        pane.top = gfxSearch
        pane.left = gfxList.apply { prefWidth = 420.0 }
        pane.center = VBox(8.0, Label("Preview"), controls, gfxCanvasWrap, Label("Details"), gfxDetails).apply { padding = Insets(10.0) }

        t.content = pane
        return t
    }

    private fun applyGfxFilter() {
        val q = gfxSearch.text.trim().lowercase()
        gfxFiltered = if (q.isEmpty()) gfxIndex else gfxIndex.filter {
            it.id.toString() == q || it.modelId.toString() == q || it.seqId.toString() == q
        }
        gfxList.items.setAll(gfxFiltered.take(5000).map { "${it.id} - model=${it.modelId} seq=${it.seqId}" })
    }

    private fun showGfx(g: SpotAnimLite) {
        val li = loaded ?: return
        val modelExists = li.fileIndex.modelCandidates.containsKey(g.modelId) || li.fileIndex.models.containsKey(g.modelId)
        gfxDetails.text = buildString {
            appendLine("SpotAnim (GFX) ${g.id}")
            appendLine("ModelId: ${g.modelId} (exists=$modelExists)")
            appendLine("SeqId:   ${g.seqId}")
        }
        lastGfxModel = buildMergedModel(intArrayOf(g.modelId))
        rerenderGfx()
    }

    private fun rerenderGfx() {
        val model = lastGfxModel
        if (model == null) {
            clearCanvas(gfxCanvas)
            return
        }
        WireframeRenderer.render(gfxCanvas, model, yawDeg, pitchDeg, zoom, flipY, swapYZ)
    }

    // ---------------- SEQ TAB (animation preview) ----------------

    private fun tabSequences(): Tab {
        val t = Tab("Sequences (Anims)").apply { isClosable = false }

        seqSearch.promptText = "Search by seq id (e.g. 1234)"
        seqSearch.textProperty().addListener { _, _, _ -> applySeqFilter() }

        seqList.selectionModel.selectedIndexProperty().addListener { _, _, new ->
            val idx = new?.toInt() ?: -1
            if (idx < 0 || idx >= seqFiltered.size) return@addListener
            val id = seqFiltered[idx]
            showSeqDetails(id)
            seqPlayer.setSeq(id)
        }

        val flipYChk = CheckBox("Flip Y").apply {
            isSelected = flipY
            setOnAction { flipY = isSelected; rerenderSeq() }
        }
        val swapYZChk = CheckBox("Swap Y/Z").apply {
            isSelected = swapYZ
            setOnAction { swapYZ = isSelected; rerenderSeq() }
        }
        val resetBtn = Button("Reset view").apply {
            setOnAction {
                yawDeg = 45.0
                pitchDeg = 20.0
                zoom = 1.0
                rerenderSeq()
            }
        }

        seqUseNpcModelBtn.setOnAction {
            val m = lastNpcMergedModel
            if (m == null) {
                status.text = "No NPC model selected yet. Pick an NPC first."
                return@setOnAction
            }
            seqPlayer.setModel(m, "NPC ${lastNpc?.id ?: "?"} ${lastNpc?.name ?: ""}")
            rerenderSeq()
        }

        seqModelIdsField.promptText = "Model ids (comma separated)"
        seqLoadModelBtn.setOnAction {
            val ids = seqModelIdsField.text
                .split(',', ' ', ';')
                .mapNotNull { it.trim().takeIf { s -> s.isNotEmpty() }?.toIntOrNull() }
                .toIntArray()
            if (ids.isEmpty()) return@setOnAction
            val m = buildMergedModel(ids)
            if (m == null) {
                status.text = "Could not load model(s): ${ids.joinToString(", ")}"; return@setOnAction
            }
            seqPlayer.setModel(m, "Model(s) ${ids.joinToString(",")}")
            rerenderSeq()
        }

        seqPlayPauseBtn.setOnAction {
            if (seqPlayer.isPlaying()) {
                seqPlayer.pause()
                seqPlayPauseBtn.text = "Play"
            } else {
                seqPlayer.play()
                seqPlayPauseBtn.text = "Pause"
            }
        }
        seqStopBtn.setOnAction {
            seqPlayer.stop()
            seqPlayPauseBtn.text = "Play"
            rerenderSeq()
        }

        seqSpeed.valueProperty().addListener { _, _, _ -> seqPlayer.speed = seqSpeed.value }
        seqLoopChk.selectedProperty().addListener { _, _, _ -> seqPlayer.loop = seqLoopChk.isSelected }

        val modelRow = HBox(8.0, seqUseNpcModelBtn, seqModelIdsField, seqLoadModelBtn).apply {
            alignment = Pos.CENTER_LEFT
        }
        HBox.setHgrow(seqModelIdsField, Priority.ALWAYS)

        val quickSeqField = TextField().apply {
            promptText = "Seq id…"
            prefWidth = 110.0
        }
        val quickPlayBtn = Button("Play id")
        quickPlayBtn.setOnAction {
            val id = quickSeqField.text.trim().toIntOrNull() ?: return@setOnAction
            openSeqAndPlay(id)
        }
        quickSeqField.setOnAction { quickPlayBtn.fire() }

        val quickRow = HBox(8.0, Label("Quick"), quickSeqField, quickPlayBtn).apply {
            alignment = Pos.CENTER_LEFT
        }

        val playRow = HBox(10.0, seqPlayPauseBtn, seqStopBtn, seqLoopChk, Label("Speed"), seqSpeed, seqFrameLbl).apply {
            alignment = Pos.CENTER_LEFT
        }

        val viewRow = HBox(10.0, flipYChk, swapYZChk, resetBtn).apply { alignment = Pos.CENTER_LEFT }

        val seqCanvasWrap = resizableCanvas(seqCanvas) { rerenderSeq() }
        installCanvasControls(seqCanvas) { rerenderSeq() }

        seqDetails.isEditable = false
        seqDetails.prefHeight = 180.0
        VBox.setVgrow(seqDetails, Priority.NEVER)

        val right = VBox(
            8.0,
            Label("Model"),
            modelRow,
            quickRow,
            Label("Preview"),
            viewRow,
            playRow,
            seqCanvasWrap,
            Label("Details"),
            seqDetails
        ).apply { padding = Insets(10.0) }

        val split = SplitPane().apply {
            items += seqList.apply { prefWidth = 240.0 }
            items += right
            setDividerPositions(0.22)
        }

        val pane = BorderPane().apply { padding = Insets(10.0) }
        pane.top = seqSearch
        pane.center = split

        t.content = pane
        return t
    }

    private fun applySeqFilter() {
        val q = seqSearch.text.trim()
        seqFiltered = if (q.isEmpty()) seqIndex else seqIndex.filter { it.toString().contains(q) }
        seqList.items.setAll(seqFiltered.take(5000).map { it.toString() })
    }

    private fun showSeqDetails(seqId: Int) {
        val li = loaded ?: return
        val seq = li.seqs[seqId]
        if (seq == null) {
            seqDetails.text = "Seq $seqId not found."
            return
        }

        val frameFiles = frameFilesForSeq(seqId)
        val missing = frameFiles.filter { !li.fileIndex.frames.containsKey(it) }

        seqDetails.text = buildString {
            appendLine("Seq $seqId")
            appendLine("Frames: ${seq.frameIds.size}")
            appendLine("FrameLengths: ${seq.frameLengths.size} (ticks)")
            appendLine()
            appendLine("Frame-file deps (${frameFiles.size}): ${frameFiles.joinToString(", ")}")
            if (missing.isNotEmpty()) appendLine("Missing frame files for: ${missing.joinToString(", ")}")
            appendLine()
            appendLine("Tip: NPC stand/walk/attack fields are sequence IDs.")
        }
    }

    private fun rerenderSeq() {
        val model = seqPlayer.currentModel()
        if (model == null) {
            clearCanvas(seqCanvas)
            return
        }
        WireframeRenderer.render(seqCanvas, model, yawDeg, pitchDeg, zoom, flipY, swapYZ)
    }

    private fun openSeqAndPlay(seqId: Int) {
        if (seqId < 0) return
        // Only auto-use the NPC model if the Sequences tab doesn't already have a model.
        if (seqPlayer.currentModel() == null) {
            val m = lastNpcMergedModel
            if (m != null) seqPlayer.setModel(m, "NPC ${lastNpc?.id ?: "?"} ${lastNpc?.name ?: ""}")
        }
        seqPlayer.setSeq(seqId)
        tabs.selectionModel.select(seqTabIndex)

        if (seqFiltered.isEmpty() && seqIndex.isNotEmpty()) applySeqFilter()
        val idx = seqFiltered.indexOf(seqId)
        if (idx >= 0) {
            seqList.selectionModel.select(idx)
            seqList.scrollTo(max(0, idx - 5))
        } else {
            showSeqDetails(seqId)
        }

        seqPlayer.play()
        seqPlayPauseBtn.text = "Pause"
    }

    // ---------------- model helpers ----------------

    private fun buildMergedModel(modelIds: IntArray): RsModel? {
        val li = loaded ?: return null
        if (modelIds.isEmpty()) return null

        val parts = mutableListOf<RsModel>()
        for (mid in modelIds) {
            decodeModel(mid, li)?.let { parts += it }
        }

        if (parts.isEmpty()) return null
        return if (parts.size == 1) parts[0] else RsModelMerger.merge(parts)
    }

    private fun decodeModel(modelId: Int, li: AppController.Loaded): RsModel? {
        // Prefer any discovered candidates; fallback to strict numeric index.
        val candidates = li.fileIndex.modelCandidates[modelId]
            ?: li.fileIndex.models[modelId]?.let { listOf(it) }
            ?: return null

        for (file in candidates.distinctBy { it.absolutePath }) {
            try {
                val bytes = file.readBytes()
                val rawCandidates = GzipUtil.expandCandidates(bytes)
                for (raw in rawCandidates) {
                    try {
                        return RsModelDecoder.decode(raw)
                    } catch (_: Throwable) {
                        // try next candidate
                    }
                }
            } catch (_: Throwable) {
                // try next file
            }
        }
        return null
    }

    // ---------------- helpers ----------------

    private fun cachePickRow(title: String, field: TextField, onPick: () -> Unit): VBox {
        val label = Label(title)
        val btn = Button("Browse").apply { setOnAction { onPick() } }
        field.promptText = "C:/.../"
        val row = HBox(8.0, field, btn)
        HBox.setHgrow(field, Priority.ALWAYS)
        return VBox(4.0, label, row)
    }

    private fun pickDir(stage: Stage): File? {
        val chooser = DirectoryChooser()
        chooser.title = "Select folder"
        return chooser.showDialog(stage)
    }

    private fun frameFilesForSeq(seqId: Int): Set<Int> {
        val li = loaded ?: return emptySet()
        val seq = li.seqs[seqId] ?: return emptySet()

        val set = linkedSetOf<Int>()
        for (packed in seq.frameIds) {
            val file = (packed ushr 16) and 0xFFFF
            if (file != 0xFFFF && file != 0) set.add(file)
        }
        return set
    }

    private fun npcDefinitionSnippet(n: NpcLite): String {
        fun intArrayToString(a: IntArray): String = a.joinToString(prefix = "[", postfix = "]")
        val actionsStr = n.actions.joinToString(prefix = "[", postfix = "]") { a ->
            if (a == null) "null" else "\"${cleanText(a)}\""
        }
        return buildString {
            append("NpcDefinition{")
            append("npcId=").append(n.id)
            append(", combatLevel=").append(n.combatLevel)
            append(", name='").append(cleanText(n.name)).append("'")
            append(", actions=").append(actionsStr)
            append(", walkAnim=").append(n.walkAnim)
            append(", turn180Anim=").append(n.turn180Anim)
            append(", turn90RightAnim=").append(n.turn90RightAnim)
            append(", walkBackAnim=").append(n.walkBackAnim)
            append(", walkLeftAnim=").append(n.walkLeftAnim)
            append(", walkRightAnim=").append(n.walkRightAnim)
            append(", size=").append(n.size)
            append(", standAnim=").append(n.standAnim)
            append(", childrenIDs=").append(intArrayToString(n.childrenIDs))
            append(", models=").append(intArrayToString(n.models))
            append('}')
        }
    }

    /**
     * Cleans RS strings for UI (removes common markup tags and non-printable chars).
     * The raw decoded values are still available in the data objects.
     */
    private fun cleanText(s: String): String {
        // Strip common client tags like <col=...>, <shad=...>, <br>, etc.
        val noTags = s.replace(Regex("<[^>]+>"), "")
        val sb = StringBuilder(noTags.length)
        for (c in noTags) {
            if (c == '\n' || c == '\r' || c == '\t' || c.code >= 32) sb.append(c)
        }
        return sb.toString().trim()
    }

    private fun resizableCanvas(canvas: Canvas, onRedraw: () -> Unit): javafx.scene.layout.StackPane {
        val wrap = javafx.scene.layout.StackPane(canvas)
        VBox.setVgrow(wrap, Priority.ALWAYS)
        // Canvas is not resizable by default, so bind it to the wrapper.
        canvas.widthProperty().bind(wrap.widthProperty())
        canvas.heightProperty().bind(wrap.heightProperty())
        wrap.widthProperty().addListener { _, _, _ -> onRedraw() }
        wrap.heightProperty().addListener { _, _, _ -> onRedraw() }
        return wrap
    }

    private fun installCanvasControls(canvas: Canvas, onChange: () -> Unit) {
        canvas.setOnScroll { e ->
            zoom = (zoom * if (e.deltaY > 0) 1.1 else 0.9).coerceIn(0.1, 10.0)
            onChange()
        }

        var lastX = 0.0
        var lastY = 0.0
        canvas.setOnMousePressed { e -> lastX = e.x; lastY = e.y }
        canvas.setOnMouseDragged { e ->
            val dx = e.x - lastX
            val dy = e.y - lastY
            lastX = e.x
            lastY = e.y
            yawDeg += dx * 0.6
            pitchDeg = (pitchDeg + dy * 0.6).coerceIn(-89.0, 89.0)
            onChange()
        }
    }

    private fun clearCanvas(canvas: Canvas) {
        val g = canvas.graphicsContext2D
        g.clearRect(0.0, 0.0, canvas.width, canvas.height)
        // background to match the rest of the app
        g.fill = javafx.scene.paint.Color.rgb(16, 16, 16)
        g.fillRect(0.0, 0.0, canvas.width, canvas.height)
        // center marker
        g.stroke = javafx.scene.paint.Color.web("#5577ff")
        val cx = canvas.width / 2.0
        val cy = canvas.height / 2.0
        g.strokeLine(cx - 6, cy, cx + 6, cy)
        g.strokeLine(cx, cy - 6, cx, cy + 6)
    }

    // ---------------- sequence player ----------------

    private inner class SeqPlayer {
        private val frameCache = HashMap<Int, FrameGroup>()

        private var model: RsModel? = null
        private var currentSeqId: Int = -1
        private var playing = false

        var loop: Boolean = true
        var speed: Double = 1.0

        private var frameIndex = 0
        private var frameTimeLeftMs = 0.0
        private var lastNanos: Long = 0

        private val timer = object : AnimationTimer() {
            override fun handle(now: Long) {
                val li = loaded ?: return
                val seq = li.seqs[currentSeqId] ?: return
                val m = model ?: return

                if (lastNanos == 0L) lastNanos = now
                val dtMs = (now - lastNanos) / 1_000_000.0
                lastNanos = now

                if (!playing) return
                if (seq.frameIds.isEmpty()) return

                frameTimeLeftMs -= dtMs
                if (frameTimeLeftMs > 0) return

                // step
                frameIndex++
                if (frameIndex >= seq.frameIds.size) {
                    if (loop) frameIndex = 0 else {
                        playing = false
                        seqPlayPauseBtn.text = "Play"
                        return
                    }
                }

                try {
                    applyFrame(li, seq, m)
                } catch (t: Throwable) {
                    playing = false
                    stop()
                    status.text = "Seq $currentSeqId failed: ${t.message ?: t::class.java.simpleName}"
                }
            }
        }

        fun setModel(newModel: RsModel, label: String) {
            // Keep a fresh model instance so we never mutate your NPC preview instance.
            model = newModel.copyForAnimation()
            frameIndex = 0
            frameTimeLeftMs = 0.0
            lastNanos = 0
            status.text = "Seq model: $label"
            // refresh current frame (if any)
            val li = loaded ?: return
            val seq = li.seqs[currentSeqId]
            if (seq != null) applyFrame(li, seq, model!!)
        }

        fun setSeq(seqId: Int) {
            currentSeqId = seqId
            frameIndex = 0
            frameTimeLeftMs = 0.0
            lastNanos = 0
            val li = loaded ?: return
            val seq = li.seqs[currentSeqId] ?: return
            val m = model ?: return
            if (seq.frameIds.isNotEmpty()) applyFrame(li, seq, m)
        }

        fun play() {
            val li = loaded ?: return
            if (currentSeqId < 0) {
                status.text = "Pick a sequence first."; return
            }
            val seq = li.seqs[currentSeqId]
            if (seq == null) {
                status.text = "Seq $currentSeqId not loaded."; return
            }
            if (model == null) {
                status.text = "Set a model first (Use current NPC model / Load model(s))."; return
            }
            if (seq.frameIds.isEmpty()) {
                status.text = "Seq $currentSeqId has no frames."; return
            }
            playing = true
            timer.start()
            try {
                applyFrame(li, seq, model!!)
            } catch (t: Throwable) {
                playing = false
                status.text = "Seq $currentSeqId failed: ${t.message ?: t::class.java.simpleName}"
            }
        }

        fun pause() {
            playing = false
        }

        fun stop() {
            playing = false
            frameIndex = 0
            frameTimeLeftMs = 0.0
            lastNanos = 0
            timer.stop()
            model?.resetVertices()
            seqFrameLbl.text = "Frame: -"
            rerenderSeq()
        }

        fun isPlaying(): Boolean = playing

        fun currentModel(): RsModel? = model

        private fun applyFrame(li: AppController.Loaded, seq: com.vykna.importer.defs.seq.Seq667, m: RsModel) {
            if (seq.frameIds.isEmpty()) {
                m.resetVertices(); rerenderSeq(); return
            }

            // UI label
            seqFrameLbl.text = "Frame: ${frameIndex + 1}/${seq.frameIds.size}"

            // timing (RS tick ~600ms)
            val tick = seq.frameLengths.getOrElse(frameIndex) { 1 }.coerceAtLeast(1)
            frameTimeLeftMs = (tick * 600.0) / speed

            val packed = seq.frameIds[frameIndex]
            val fileId = (packed ushr 16) and 0xFFFF
            val frameId = packed and 0xFFFF
            if (fileId == 0 || fileId == 0xFFFF) {
                m.resetVertices(); rerenderSeq(); return
            }

            val group = frameCache.getOrPut(fileId) {
                val f = li.fileIndex.frames[fileId] ?: throw IllegalStateException("Missing frame file $fileId")
                val rawCandidates = GzipUtil.expandCandidates(f.readBytes())
                var last: Throwable? = null
                for (raw in rawCandidates) {
                    try {
                        return@getOrPut FrameGroupDecoder.decode(raw)
                    } catch (t: Throwable) {
                        last = t
                    }
                }
                throw IllegalStateException("Could not decode frame file $fileId: ${last?.message}")
            }

            val frame = group.frames[frameId]
            if (frame != null) {
                try {
                    // RsAnimator.apply() resets vertices internally.
                    RsAnimator.apply(m, frame)
                } catch (t: Throwable) {
                    Log.warn("[SeqPreview] apply failed file=$fileId frame=$frameId: ${t.message}")
                    m.resetVertices()
                }
            } else {
                m.resetVertices()
            }

            rerenderSeq()
        }
    }
}

/**
 * Create a safe copy for animation playback.
 *
 * RsAnimator mutates vertex arrays, so we must not animate the same instance
 * shown on other tabs. Creating a new RsModel also creates a new reset-baseline
 * (internal originals) from the cloned vertices.
 */
fun RsModel.copyForAnimation(): RsModel {
    val skin = if (this.vertexSkin.isNotEmpty()) this.vertexSkin.clone() else IntArray(0)
    return RsModel(
        vx = this.vx.clone(),
        vy = this.vy.clone(),
        vz = this.vz.clone(),
        fx = this.fx, // faces are never mutated by the animator
        fy = this.fy,
        fz = this.fz,
        vertexSkin = skin
    )
}

