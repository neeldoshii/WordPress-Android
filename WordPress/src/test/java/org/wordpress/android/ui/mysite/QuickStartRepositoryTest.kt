package org.wordpress.android.ui.mysite

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.wordpress.android.BaseUnitTest
import org.wordpress.android.fluxc.model.SiteModel
import org.wordpress.android.fluxc.store.QuickStartStore
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTask.CREATE_SITE
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTask.ENABLE_POST_SHARING
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTask.PUBLISH_POST
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTask.UPDATE_SITE_TITLE
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTaskType.CUSTOMIZE
import org.wordpress.android.fluxc.store.QuickStartStore.QuickStartTaskType.GROW
import org.wordpress.android.ui.mysite.QuickStartRepository.QuickStartCategory
import org.wordpress.android.ui.quickstart.QuickStartTaskDetails
import org.wordpress.android.ui.quickstart.QuickStartTaskDetails.CREATE_SITE_TUTORIAL
import org.wordpress.android.ui.quickstart.QuickStartTaskDetails.PUBLISH_POST_TUTORIAL
import org.wordpress.android.ui.quickstart.QuickStartTaskDetails.SHARE_SITE_TUTORIAL
import org.wordpress.android.util.QuickStartUtilsWrapper

class QuickStartRepositoryTest : BaseUnitTest() {
    @Mock lateinit var quickStartStore: QuickStartStore
    @Mock lateinit var quickStartUtils: QuickStartUtilsWrapper
    @Mock lateinit var selectedSiteRepository: SelectedSiteRepository
    private lateinit var site: SiteModel
    private lateinit var quickStartRepository: QuickStartRepository
    private lateinit var models: MutableList<List<QuickStartCategory>>
    private val siteId = 1

    @Before
    fun setUp() {
        quickStartRepository = QuickStartRepository(quickStartStore, quickStartUtils, selectedSiteRepository)
        models = mutableListOf()
        quickStartRepository.quickStartModel.observeForever { models.add(it) }
        site = SiteModel()
        site.id = siteId
    }

    @Test
    fun `model is empty when not started`() {
        assertThat(models).isEmpty()
    }

    @Test
    fun `refresh loads model when quick start in progress and model not yet refreshed`() {
        whenever(quickStartUtils.isQuickStartInProgress()).thenReturn(true)
        whenever(selectedSiteRepository.getSelectedSite()).thenReturn(site)
        initStore()

        quickStartRepository.refreshIfNecessary()

        assertModel(2)
    }

    @Test
    fun `refresh doesn't reload model when already initialized`() {
        whenever(quickStartUtils.isQuickStartInProgress()).thenReturn(true)
        whenever(selectedSiteRepository.getSelectedSite()).thenReturn(site)
        initStore()

        quickStartRepository.refreshIfNecessary()

        assertModel(2)

        quickStartRepository.refreshIfNecessary()

        assertModel(2)
    }

    @Test
    fun `refresh doesn't load model when quick start not in progress`() {
        whenever(quickStartUtils.isQuickStartInProgress()).thenReturn(false)

        quickStartRepository.refreshIfNecessary()

        assertThat(models).isEmpty()
    }

    @Test
    fun `start marsk CREATE_SITE as done and loads model`() {
        whenever(selectedSiteRepository.getSelectedSite()).thenReturn(site)
        initStore()

        quickStartRepository.startQuickStart()

        verify(quickStartStore).setDoneTask(siteId.toLong(), CREATE_SITE, true)
        assertModel(2)
    }

    private fun initStore() {
        whenever(quickStartStore.getUncompletedTasksByType(siteId.toLong(), CUSTOMIZE)).thenReturn(listOf(CREATE_SITE))
        whenever(quickStartStore.getCompletedTasksByType(siteId.toLong(), CUSTOMIZE)).thenReturn(
                listOf(
                        UPDATE_SITE_TITLE
                )
        )
        whenever(
                quickStartStore.getUncompletedTasksByType(
                        siteId.toLong(),
                        GROW
                )
        ).thenReturn(listOf(ENABLE_POST_SHARING))
        whenever(quickStartStore.getCompletedTasksByType(siteId.toLong(), GROW)).thenReturn(listOf(PUBLISH_POST))
    }

    private fun assertModel(size: Int) {
        assertThat(models).hasSize(size)
        models.last().let { categories ->
            assertThat(categories).hasSize(2)
            assertThat(categories[0].taskType).isEqualTo(CUSTOMIZE)
            assertThat(categories[0].uncompletedTasks).containsExactly(CREATE_SITE_TUTORIAL)
            assertThat(categories[0].completedTasks).containsExactly(QuickStartTaskDetails.UPDATE_SITE_TITLE)
            assertThat(categories[1].taskType).isEqualTo(GROW)
            assertThat(categories[1].uncompletedTasks).containsExactly(SHARE_SITE_TUTORIAL)
            assertThat(categories[1].completedTasks).containsExactly(PUBLISH_POST_TUTORIAL)
        }
    }

    /*
    return !quickStartStore.getQuickStartCompleted(AppPrefs.getSelectedSite().toLong()) &&
                    quickStartStore.hasDoneTask(AppPrefs.getSelectedSite().toLong(), CREATE_SITE)
     */
}
