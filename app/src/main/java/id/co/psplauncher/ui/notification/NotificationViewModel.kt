package id.co.psplauncher.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.co.psplauncher.data.network.Resource
import id.co.psplauncher.data.network.notification.NotificationRepository
import id.co.psplauncher.data.network.response.NotificationDetailResponse
import id.co.psplauncher.data.network.response.NotificationListResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableLiveData<Resource<NotificationListResponse>>()
    val notifications: LiveData<Resource<NotificationListResponse>> = _notifications

    private val _notificationDetail = MutableLiveData<Resource<NotificationDetailResponse>>()
    val notificationDetail: LiveData<Resource<NotificationDetailResponse>> = _notificationDetail

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var currentPage = 0
    private var isLastPage = false

    fun loadNotifications(page: Int = 0, size: Int = 10) {
        if (page == 0) {
            _isLoading.value = true
        }
        
        viewModelScope.launch {
            _notifications.value = Resource.Loading
            val result = repository.getNotifications(page, size)
            _notifications.value = result
            
            if (result is Resource.Success) {
                currentPage = page
                isLastPage = result.value.last
            }
            
            _isLoading.value = false
        }
    }

    fun loadNotificationDetail(id: String) {
        viewModelScope.launch {
            _notificationDetail.value = Resource.Loading
            _notificationDetail.value = repository.getNotificationDetail(id)
        }
    }

    fun loadNextPage() {
        if (!isLastPage && _isLoading.value != true) {
            loadNotifications(currentPage + 1)
        }
    }

    fun refresh() {
        loadNotifications(0)
    }
}
