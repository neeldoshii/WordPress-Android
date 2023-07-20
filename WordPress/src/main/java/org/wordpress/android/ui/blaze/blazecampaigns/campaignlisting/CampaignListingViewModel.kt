package org.wordpress.android.ui.blaze.blazecampaigns.campaignlisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.wordpress.android.R
import org.wordpress.android.ui.blaze.BlazeFeatureUtils
import org.wordpress.android.ui.utils.UiString
import javax.inject.Inject

@HiltViewModel
class CampaignListingViewModel @Inject constructor(
    private val blazeFeatureUtils: BlazeFeatureUtils,
) : ViewModel() {
    private val _uiState = MutableLiveData<CampaignListingUiState>()
    val uiState: LiveData<CampaignListingUiState> = _uiState

    fun start(campaignListingPageSource: CampaignListingPageSource) {
        blazeFeatureUtils.trackCampaignListingPageShown(campaignListingPageSource)
        _uiState.value = CampaignListingUiState.Loading
    }

    fun showNoCampaigns() {
        _uiState.value = CampaignListingUiState.Error(
            title = UiString.UiStringRes(R.string.campaign_listing_page_no_campaigns_message_title),
            description = UiString.UiStringRes(R.string.campaign_listing_page_no_campaigns_message_description),
            button = CampaignListingUiState.Error.ErrorButton(
                text = UiString.UiStringRes(R.string.campaign_listing_page_no_campaigns_button_text),
                click = { }
            )
        )
    }
}

enum class CampaignListingPageSource(val trackingName: String) {
    DASHBOARD_CARD("dashboard_card"),
    MENU_ITEM("menu_item"),
    UNKNOWN("unknown")
}

