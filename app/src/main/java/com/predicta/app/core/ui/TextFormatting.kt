package com.predicta.app.core.ui

private val numberedLineRegex = Regex("""^\s*\d+\s*[\.\)\:\-]?\s*(.*)$""")

fun String.formatBackendText(): String {
    val normalized = replace("\\r\\n", "\n")
        .replace("\\n", "\n")
        .trim()

    if (normalized.isBlank()) return normalized

    return normalized
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .mapNotNull { line ->
            when {
                line.matches(Regex("""^\d+$""")) -> null
                numberedLineRegex.matches(line) -> {
                    val text = numberedLineRegex.matchEntire(line)?.groupValues?.getOrNull(1).orEmpty()
                    text.takeIf { it.isNotBlank() }?.let { "• $it" }
                }
                else -> line
            }
        }
        .joinToString("\n")
}
