package org.wordpress.android.ui.review

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.wordpress.android.ui.prefs.AppPrefsWrapper
import org.wordpress.android.viewmodel.Event
import org.wordpress.android.widgets.AppRatingDialog
import java.util.Date
import javax.inject.Inject

/**
 * Manages the logic for the flow of in-app reviews prompt.
 */
class ReviewViewModel @Inject constructor(private val appPrefsWrapper: AppPrefsWrapper) : ViewModel() {
    private val _launchReview = MutableLiveData<Event<Unit>>()
    val launchReview = _launchReview as LiveData<Event<Unit>>

    fun onPublishingPost() {
        val shouldWaitAskLaterTime = Date().time - AppRatingDialog.askLaterDate.time < AppRatingDialog.criteriaInstallMs
        if (!appPrefsWrapper.isInAppReviewsShown() && !shouldWaitAskLaterTime) {
            if (appPrefsWrapper.getPublishedPostCount() < TARGET_COUNT_POST_PUBLISHED) {
                appPrefsWrapper.incrementPublishedPostCount()
            }
            if (appPrefsWrapper.getPublishedPostCount() == TARGET_COUNT_POST_PUBLISHED) {
                _launchReview.value = Event(Unit)
                appPrefsWrapper.setInAppReviewsShown()
            }
        }
    }

    companion object {
        @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
        const val TARGET_COUNT_POST_PUBLISHED = 2
    }
}
