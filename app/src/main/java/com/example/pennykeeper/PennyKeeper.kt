import androidx.compose.runtime.Composable
import com.example.pennykeeper.data.repository.CategoryRepository
import com.example.pennykeeper.data.repository.ExpenseRepository
import com.example.pennykeeper.data.repository.SettingsRepository
import com.example.pennykeeper.data.repository.ThemeRepository
import com.example.pennykeeper.ui.navigation.Navigation
import com.example.pennykeeper.ui.theme.PennykeeperTheme

@Composable
fun PennyKeeper(expenseRepository: ExpenseRepository, settingsRepository: SettingsRepository, categoryRepository: CategoryRepository, themeRepository: ThemeRepository) {
    PennykeeperTheme {
        Navigation(expenseRepository,settingsRepository, categoryRepository, themeRepository)
    }
}