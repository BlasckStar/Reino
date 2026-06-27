package com.blasck.reino.presentation.layout

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.blasck.reino.R
import com.blasck.reino.domain.model.CharacterImportIssue
import com.blasck.reino.presentation.components.CoilImage
import com.blasck.reino.presentation.screen.model.CharacterModel

private enum class CharacterSection(val label: String, val icon: Int) {
    SUMMARY("Resumo", R.drawable.ic_menu_summary),
    COMBAT("Combate", R.drawable.ic_menu_combat),
    SKILLS("Pericias", R.drawable.ic_menu_skills),
    EQUIPMENT("Itens", R.drawable.ic_menu_equipment),
    MORE("Perfil", R.drawable.ic_menu_profile),
}

@Composable
fun CharacterLayout(
    model: CharacterModel,
    hasBackup: Boolean,
    onEditSession: () -> Unit,
    onSearch: () -> Unit,
    onUpdateSheet: () -> Unit,
    onRestoreBackup: () -> Unit,
) {
    var selected by remember { mutableStateOf(CharacterSection.SUMMARY) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onSearch) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar na ficha",
                )
            }
        },
        bottomBar = {
            NavigationBar {
                CharacterSection.entries.forEach { section ->
                    NavigationBarItem(
                        selected = selected == section,
                        onClick = { selected = section },
                        icon = {
                            androidx.compose.material3.Icon(
                                painter = painterResource(section.icon),
                                contentDescription = section.label,
                            )
                        },
                        label = { Text(section.label) },
                    )
                }
            }
        },
    ) { padding ->
        when (selected) {
            CharacterSection.SUMMARY ->
                Summary(
                    model = model,
                    hasBackup = hasBackup,
                    onRestoreBackup = onRestoreBackup,
                    modifier = Modifier.padding(padding),
                )
            CharacterSection.COMBAT -> Combat(model, Modifier.padding(padding))
            CharacterSection.SKILLS -> Skills(model, Modifier.padding(padding))
            CharacterSection.EQUIPMENT -> Equipment(model, Modifier.padding(padding))
            CharacterSection.MORE -> More(model, Modifier.padding(padding))
        }
    }
}

@Composable
private fun Summary(
    model: CharacterModel,
    hasBackup: Boolean,
    onRestoreBackup: () -> Unit,
    modifier: Modifier,
) {
    var showRestoreDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Header(model)
            if (hasBackup) {
                OutlinedButton(
                    onClick = { showRestoreDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).heightIn(min = 48.dp),
                ) {
                    Text("Restaurar backup anterior")
                }
            }
        }
        item {
            SectionTitle("Atributos")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AttributeCard("ST", model.status.strength, Modifier.weight(1f))
                AttributeCard("DX", model.status.dexterity, Modifier.weight(1f))
                AttributeCard("IQ", model.status.intelligence, Modifier.weight(1f))
                AttributeCard("HT", model.status.constitution, Modifier.weight(1f))
            }
        }
        item {
            SectionTitle("Recursos")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResourceSummaryCard(
                        label = "P. Fadiga",
                        value = model.status.fatigue.withMaximum(model.status.maxFatigue),
                        modifier = Modifier.weight(1f),
                    )
                    ResourceSummaryCard(
                        label = "P. Mana",
                        value = model.status.mana.withMaximum(model.status.maxMana),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ResourceSummaryCard(
                        label = "Vontade",
                        value = model.status.will,
                        modifier = Modifier.weight(1f),
                    )
                    ResourceSummaryCard(
                        label = "Percepção",
                        value = model.status.perception,
                        modifier = Modifier.weight(1f),
                    )
                }
                ResourceSummaryCard(
                    label = "P. Vida",
                    value = model.status.hitPoints.withMaximum(model.status.maxHitPoints),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        item {
            SectionTitle("Defesas rápidas")
            MetricGrid(
                listOf(
                    "Esquiva" to model.status.dodgeBAL,
                    "Aparar" to model.status.parry.firstOrNull()?.value.orEmpty(),
                    "Bloqueio" to model.status.block,
                    "Deslocamento" to model.status.basicDislocation,
                    "Carga básica" to model.status.baseWeight,
                    "Velocidade" to model.status.basicSpeed,
                ),
            )
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restaurar backup?") },
            text = {
                Text(
                    "A ficha atual sera substituida pelo backup criado antes da ultima atualizacao.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestoreDialog = false
                        onRestoreBackup()
                    },
                ) {
                    Text("Restaurar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancelar")
                }
            },
        )
    }
}

@Composable
private fun Header(model: CharacterModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            if (model.image.isNotBlank()) {
                CoilImage(
                    imageUrl = model.image,
                    maxHeight = 220.dp,
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(
                model.information.name.ifBlank { "Personagem sem nome" },
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                listOf(model.information.race, model.information.kingdom)
                    .filter(String::isNotBlank)
                    .joinToString(" • "),
                style = MaterialTheme.typography.titleMedium,
            )
            if (model.information.player.isNotBlank()) {
                Text("Jogador: ${model.information.player}")
            }
        }
    }
}

@Composable
private fun Combat(model: CharacterModel, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            SectionTitle("Ataques e defesas")
            MetricGrid(
                listOf(
                    "Dano GDP" to model.status.damageGDP,
                    "Dano BAL" to model.status.damageBAL,
                    "Esquiva" to model.status.dodgeBAL,
                    "Bloqueio" to model.status.block,
                ),
            )
        }
        item { SectionTitle("Aparar") }
        emptyOrItems(
            model.status.parry,
            "Nenhuma defesa de aparar preenchida.",
        ) { ListRow(it.name, it.value) }
        item { SectionTitle("Armas corpo a corpo") }
        emptyOrItems(model.meleeWeapon.list, "Nenhuma arma corpo a corpo preenchida.") {
            ExpandableRow(
                title = it.name,
                trailing = it.gdp.ifBlank { it.bal },
                subtitle =
                    listOf(
                        detailLine("GDP", it.gdp),
                        detailLine("BAL", it.bal),
                    ).filter(String::isNotBlank).joinToString("  |  "),
                details =
                    listOf(
                        detailLine("Qualidade", it.quality),
                        detailLine("Tipo GDP", it.gdpType),
                        detailLine("Tipo BAL", it.balType),
                        it.notes,
                    ),
            )
        }
        item { SectionTitle("Armas de longo alcance") }
        emptyOrItems(model.rangedWeapon.list, "Nenhuma arma de longo alcance preenchida.") {
            ExpandableRow(
                title = it.name,
                trailing = it.damage,
                subtitle =
                    listOf(
                        detailLine("Prec", it.precision),
                        detailLine(
                            "Alc",
                            listOf(it.halfDistance, it.maxDistance)
                                .filter(String::isNotBlank)
                                .joinToString("/"),
                        ),
                        detailLine("TR", it.tr),
                    ).filter(String::isNotBlank).joinToString("  |  "),
                details =
                    listOf(
                        "Precisão ${it.precision}",
                        "Alcance ${it.halfDistance}/${it.maxDistance}",
                        detailLine("Cadencia", it.fireRate),
                        detailLine("CDT", it.cdt),
                        detailLine("Recuo", it.recoil),
                        detailLine("ST", it.st),
                        "TR ${it.tr}",
                        it.notes,
                    ),
            )
        }
        item { SectionTitle("Proteção") }
        val defenses =
            listOf(
                model.status.headDefense,
                model.status.bodyDefense,
                model.status.armDefense,
                model.status.handsDefense,
                model.status.legsDefense,
                model.status.feetDefense,
            ).filter { it.part.isNotBlank() }
        emptyOrItems(defenses, "Nenhuma proteção preenchida.") {
            ListRow(it.part, "RD ${it.totalRD} • DP ${it.totalDP}")
        }
        item { SectionTitle("Defesas detalhadas") }
        emptyOrItems(defenses, "Nenhuma defesa detalhada preenchida.") {
            DefenseRow(it)
        }
        item { SectionTitle("Magias de batalha") }
        emptyOrItems(model.spellbook.list, "Nenhuma magia preenchida.") {
            ExpandableRow(
                title = it.name,
                trailing = it.level,
                subtitle =
                    listOf(
                        detailLine("Dificuldade", it.difficulty),
                        detailLine("Classe", it.spellClass),
                        detailLine("Custo", it.cost),
                    ).filter(String::isNotBlank).joinToString("  |  "),
                details =
                    listOf(
                        detailLine("Pagina", it.page),
                        detailLine("Duracao", it.duration),
                        detailLine("Custo para conjurar", it.castingCost),
                        detailLine("Custo para manter", it.maintenanceCost),
                        detailLine("Tempo de conjuracao", it.castingTime),
                        it.notes,
                    ),
            )
        }
    }
}

@Composable
private fun Skills(model: CharacterModel, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { SectionTitle("Perícias (${model.expertise.list.size})") }
        emptyOrItems(model.expertise.list, "Nenhuma perícia preenchida na ficha.") {
            ExpandableRow(
                title = it.name,
                trailing = it.nh,
                subtitle =
                    listOf(it.difficultType, it.difficultLevel, it.difficultExtra)
                        .filter(String::isNotBlank)
                        .joinToString(" / "),
                details = listOf("Custo: ${it.cost}", it.description),
            )
        }
    }
}

@Composable
private fun Equipment(model: CharacterModel, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { SectionTitle("Inventário") }
        emptyOrItems(model.inventory.list, "Nenhum item preenchido.") {
            ExpandableRow(
                it.name,
                it.weight,
                "Quantidade: ${it.quantity.ifBlank { "1" }}",
                listOf("Valor: ${it.value}", it.description),
            )
        }
        item { SectionTitle("Armaduras") }
        emptyOrItems(model.armorList.list, "Nenhuma armadura preenchida.") {
            ExpandableRow(
                it.name,
                "RD ${it.defenseRD}",
                details = listOf("DP ${it.defenseDP}", "Peso ${it.weight}", it.description),
            )
        }
        item { SectionTitle("Itens mágicos") }
        emptyOrItems(model.magicItems.list, "Nenhum item mágico preenchido.") {
            ExpandableRow(
                it.name,
                "Mana ${it.mana}",
                details = listOf("Peso ${it.weight}", "Custo ${it.cost}", it.description),
            )
        }
        item {
            SectionTitle("Moedas")
            MetricGrid(
                listOf(
                    "Ouro" to model.money.gold,
                    "Prata" to model.money.silver,
                    "Cobre" to model.money.copper,
                ),
            )
        }
    }
}

@Composable
private fun More(model: CharacterModel, modifier: Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            SectionTitle("Identidade e raca")
            MetricGrid(
                listOf(
                    "Raca" to model.information.race,
                    "Reino" to model.information.kingdom,
                    "Idade" to model.information.age,
                    "Aparencia" to model.information.appearance,
                    "Altura" to model.information.height,
                    "Peso" to model.information.weight,
                ),
            )
        }
        item { SectionTitle("Vantagens (${model.raceAndAdvantages.list.size})") }
        emptyOrItems(model.raceAndAdvantages.list, "Nenhuma vantagem preenchida.") {
            ExpandableRow(
                title = it.name,
                trailing = it.cost,
                subtitle =
                    listOf(
                        detailLine("Pontos", it.points),
                        detailLine("Chance", it.chance),
                        detailLine("Multiplicador", it.multiplier),
                        it.multiplierType,
                    ).filter(String::isNotBlank).joinToString("  |  "),
                details = listOf(it.description),
            )
        }
        item { SectionTitle("Desvantagens (${model.disadvantagesAndPeculiarities.disadvantages.size})") }
        emptyOrItems(model.disadvantagesAndPeculiarities.disadvantages, "Nenhuma desvantagem preenchida.") {
            ExpandableRow(
                title = it.name,
                trailing = it.cost,
                details = listOf(it.description),
            )
        }
        item { SectionTitle("Peculiaridades (${model.disadvantagesAndPeculiarities.peculiarities.size})") }
        emptyOrItems(model.disadvantagesAndPeculiarities.peculiarities, "Nenhuma peculiaridade preenchida.") {
            ExpandableRow(
                title = it.name,
                trailing = it.cost,
                details = listOf(it.description),
            )
        }
        item {
            SectionTitle("Modificadores de reacao")
            MetricGrid(
                listOf(
                    "Aparencia" to model.reactionModifiers.appearance,
                    "Status" to model.reactionModifiers.status,
                    "Reputacao" to model.reactionModifiers.reputation,
                    "Total" to model.reactionModifiers.totalCost,
                ),
            )
        }
        emptyOrItems(model.reactionModifiers.additional, "Nenhum modificador adicional preenchido.") {
            ExpandableRow(
                title = it.name,
                trailing = it.type,
                details = listOf(detailLine("Custo", it.cost)),
            )
        }
        item { SectionTitle("Anotacoes") }
        emptyOrItems(model.annotations.list, "Nenhuma anotacao registrada.") {
            ListRow(it, "")
        }
        item {
            SectionTitle("Resumo de pontos")
            MetricGrid(
                listOf(
                    "Atributos" to model.pointResume.status,
                    "Vantagens" to model.pointResume.advantages,
                    "Desvantagens" to model.pointResume.disadvantages,
                    "Raca" to model.pointResume.race,
                    "Peculiaridades" to model.pointResume.peculiarities,
                    "Pericias" to model.pointResume.expertise,
                    "Total" to model.pointResume.total,
                ),
            )
        }
    }
}

private fun <T> androidx.compose.foundation.lazy.LazyListScope.emptyOrItems(
    values: List<T>,
    emptyMessage: String,
    content: @Composable (T) -> Unit,
) {
    if (values.isEmpty()) {
        item { EmptyState(emptyMessage) }
    } else {
        items(values) { content(it) }
    }
}

@Composable
private fun AttributeCard(label: String, value: String, modifier: Modifier) {
    Card(modifier = modifier.heightIn(min = 80.dp)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ErrorAwareValue(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ResourceSummaryCard(label: String, value: String, modifier: Modifier) {
    Card(
        modifier = modifier.heightIn(min = 76.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
            ErrorAwareValue(
                value = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun MetricGrid(values: List<Pair<String, String>>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        values.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { (label, value) ->
                    Card(modifier = Modifier.weight(1f).fillMaxHeight().heightIn(min = 88.dp)) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(label, style = MaterialTheme.typography.labelMedium)
                            ErrorAwareValue(value, style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                if (row.size == 1) Spacer(Modifier.weight(1f).fillMaxHeight())
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
    )
}

@Composable
private fun ListRow(title: String, trailing: String) {
    Card(modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(title, modifier = Modifier.weight(1f))
            if (trailing.isNotBlank()) ErrorAwareValue(trailing, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ExpandableRow(
    title: String,
    trailing: String = "",
    subtitle: String = "",
    details: List<String> = emptyList(),
) {
    var expanded by remember(title) { mutableStateOf(false) }
    val visibleDetails = details.filter(String::isNotBlank)
    val hasDetails = visibleDetails.isNotEmpty()
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clickable(enabled = hasDetails) { expanded = !expanded },
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium)
                    if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodySmall)
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (trailing.isNotBlank()) {
                        ErrorAwareValue(trailing, style = MaterialTheme.typography.titleLarge)
                    }
                    if (hasDetails) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (expanded) "Ocultar detalhes" else "Ver detalhes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Icon(
                                imageVector =
                                    if (expanded) {
                                        Icons.Filled.KeyboardArrowUp
                                    } else {
                                        Icons.Filled.KeyboardArrowDown
                                    },
                                contentDescription =
                                    if (expanded) {
                                        "Ocultar detalhes"
                                    } else {
                                        "Ver detalhes"
                                    },
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
            AnimatedVisibility(expanded && hasDetails) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    HorizontalDivider()
                    visibleDetails.forEach { ErrorAwareValue(it) }
                }
            }
        }
    }
}

@Composable
private fun ErrorAwareValue(
    value: String,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    fontWeight: FontWeight? = null,
) {
    if (value.hasImportError()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "!",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value.replace(CharacterImportIssue.ERROR_VALUE, "campo com erro"),
                style = style,
                fontWeight = fontWeight,
                color = MaterialTheme.colorScheme.error,
            )
        }
    } else {
        Text(
            text = value.ifBlank { "-" },
            style = style,
            fontWeight = fontWeight,
        )
    }
}

private fun String.hasImportError() = contains(CharacterImportIssue.ERROR_VALUE)

private fun String.withMaximum(maximum: String): String =
    if (isNotBlank() && maximum.isNotBlank() && maximum != "0") {
        "$this / $maximum"
    } else {
        this
    }

@Composable
private fun DefenseRow(defense: CharacterModel.StatusInformation.Defense) {
    ExpandableRow(
        title = defense.part,
        trailing =
            listOf(
                detailLine("DP", defense.totalDP),
                detailLine("RD", defense.totalRD),
            ).filter(String::isNotBlank).joinToString("  |  "),
        details =
            listOf(
                detailLine("DP natural", defense.naturalDP),
                detailLine("DP armadura", defense.armorDP),
                detailLine("DP total", defense.totalDP),
                detailLine("RD natural", defense.naturalRD),
                detailLine("RD armadura", defense.armorRD),
                detailLine("RD total", defense.totalRD),
            ),
    )
}

private fun detailLine(label: String, value: String): String =
    value.ifBlank { null }?.let { "$label: $it" }.orEmpty()

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp).padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}
