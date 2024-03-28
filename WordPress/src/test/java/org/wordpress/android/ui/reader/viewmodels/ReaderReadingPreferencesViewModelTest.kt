package org.wordpress.android.ui.reader.viewmodels

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.kotlin.argThat
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.ui.reader.models.ReaderReadingPreferences
import org.wordpress.android.ui.reader.usecases.ReaderGetReadingPreferencesSyncUseCase
import org.wordpress.android.ui.reader.usecases.ReaderSaveReadingPreferencesUseCase
import org.wordpress.android.ui.reader.viewmodels.ReaderReadingPreferencesViewModel.ActionEvent

@ExperimentalCoroutinesApi
class ReaderReadingPreferencesViewModelTest : BaseUnitTest() {
    @Mock
    lateinit var getReadingPreferences: ReaderGetReadingPreferencesSyncUseCase

    @Mock
    lateinit var saveReadingPreferences: ReaderSaveReadingPreferencesUseCase

    private val viewModelDispatcher = UnconfinedTestDispatcher(testDispatcher().scheduler)
    private lateinit var viewModel: ReaderReadingPreferencesViewModel

    private val collectedEvents = mutableListOf<ActionEvent>()

    @Before
    fun setUp() {
        whenever(getReadingPreferences()).thenReturn(DEFAULT_READING_PREFERENCES)

        viewModel = ReaderReadingPreferencesViewModel(
            getReadingPreferences,
            saveReadingPreferences,
            viewModelDispatcher,
        )

        viewModel.collectEvents()
    }

    @After
    fun tearDown() {
        viewModelDispatcher.cancel()
        collectedEvents.clear()
    }

    private fun ReaderReadingPreferencesViewModel.collectEvents() {
        actionEvents.onEach { actionEvent ->
            collectedEvents.add(actionEvent)
        }.launchIn(testScope().backgroundScope)
    }

    @Test
    fun `when ViewModel is initialized then it should emit UpdateStatusBarColor action event`() = test {
        // When
        viewModel.init()

        // Then
        val updateStatusBarColorEvent = collectedEvents.last() as ActionEvent.UpdateStatusBarColor
        assertThat(updateStatusBarColorEvent.theme).isEqualTo(DEFAULT_READING_PREFERENCES.theme)
    }

    @Test
    fun `when collecting currentReadingPreferences then it should have the initial reading preferences`() = test {
        // When
        val currentReadingPreferences = viewModel.currentReadingPreferences.first()

        // Then
        assertThat(currentReadingPreferences).isEqualTo(DEFAULT_READING_PREFERENCES)
    }

    @Test
    fun `when onThemeClick is called then it should update the theme`() = test {
        // Given
        val newTheme = ReaderReadingPreferences.Theme.OLED

        // When
        viewModel.onThemeClick(newTheme)

        // Then
        val updatedReadingPreferences = viewModel.currentReadingPreferences.first()
        assertThat(updatedReadingPreferences.theme).isEqualTo(newTheme)
    }

    @Test
    fun `when onFontFamilyClick is called then it should update the font family`() = test {
        // Given
        val newFontFamily = ReaderReadingPreferences.FontFamily.MONO

        // When
        viewModel.onFontFamilyClick(newFontFamily)

        // Then
        val updatedReadingPreferences = viewModel.currentReadingPreferences.first()
        assertThat(updatedReadingPreferences.fontFamily).isEqualTo(newFontFamily)
    }

    @Test
    fun `when onFontSizeClick is called then it should update the font size`() = test {
        // Given
        val newFontSize = ReaderReadingPreferences.FontSize.LARGE

        // When
        viewModel.onFontSizeClick(newFontSize)

        // Then
        val updatedReadingPreferences = viewModel.currentReadingPreferences.first()
        assertThat(updatedReadingPreferences.fontSize).isEqualTo(newFontSize)
    }

    @Test
    fun `when saveReadingPreferencesAndClose is called then it emits Close action event`() = test {
        // When
        viewModel.saveReadingPreferencesAndClose()

        // Then
        val closeEvent = collectedEvents.last() as ActionEvent.Close
        assertThat(closeEvent.isDirty).isFalse()
    }

    @Test
    fun `when saveReadingPreferencesAndClose is called with updated preferences then it emits Close action event`() =
        test {
            // Given
            val newTheme = ReaderReadingPreferences.Theme.SEPIA
            viewModel.onThemeClick(newTheme)

            // When
            viewModel.saveReadingPreferencesAndClose()

            // Then
            val closeEvent = collectedEvents.last() as ActionEvent.Close
            assertThat(closeEvent.isDirty).isTrue()
        }

    @Test
    fun `when saveReadingPreferencesAndClose is called with updated preferences then it saves them`() = test {
        // Given
        val newTheme = ReaderReadingPreferences.Theme.SOFT
        viewModel.onThemeClick(newTheme)

        // When
        viewModel.saveReadingPreferencesAndClose()

        // Then
        verify(saveReadingPreferences).invoke(argThat { theme == newTheme })
    }

    @Test
    fun `when closeWithoutSaving is called then it emits Close action event`() = test {
        // When
        viewModel.closeWithoutSaving()

        // Then
        val closeEvent = collectedEvents.last() as ActionEvent.Close
        assertThat(closeEvent.isDirty).isFalse()
    }

    @Test
    fun `when closeWithoutSaving is called with updated preferences then it emits Close action event`() = test {
        // Given
        val newTheme = ReaderReadingPreferences.Theme.SOFT
        viewModel.onThemeClick(newTheme)

        // When
        viewModel.closeWithoutSaving()

        // Then
        val closeEvent = collectedEvents.last() as ActionEvent.Close
        assertThat(closeEvent.isDirty).isFalse()
    }

    @Test
    fun `when closeWithoutSaving is called then it does not save the preferences`() = test {
        // When
        viewModel.closeWithoutSaving()

        // Then
        verifyNoInteractions(saveReadingPreferences)
    }

    companion object {
        private val DEFAULT_READING_PREFERENCES = ReaderReadingPreferences()
    }
}
