package com.app.iot.ui.features.auth.state

import com.app.iot.R
import com.app.iot.ui.features.common.state.ErrorState


val emailOrMobileEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_email_mobile
)

val passwordEmptyErrorState = ErrorState(
    hasError = true,
    errorMessageStringResource = R.string.login_error_msg_empty_password
)