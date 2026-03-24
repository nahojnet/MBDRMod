package com.mbdrmod.delivery.ui.screens.forms

import androidx.compose.runtime.*
import com.mbdrmod.delivery.data.model.AppConfig
import com.mbdrmod.delivery.data.model.DeliveryData
import com.mbdrmod.delivery.data.model.Anomaly

@Composable
fun FormContainerScreen(
    currentFormIndex: Int,
    delivery: DeliveryData,
    config: AppConfig,
    onUpdate: (DeliveryData) -> Unit,
    onNextForm: () -> Unit,
    onPreviousForm: () -> Unit,
    onAddAnomaly: () -> Unit,
    onUpdateAnomaly: (Long, (Anomaly) -> Anomaly) -> Unit,
    onRemoveAnomaly: (Long) -> Unit,
    onFinish: () -> Unit,
    onGoHome: () -> Unit
) {
    when (currentFormIndex) {
        -1 -> SiteSelectionScreen(
            deliverySites = config.deliverySites,
            selectedSite = delivery.deliverySite,
            onSiteSelected = { site -> onUpdate(delivery.copy(deliverySite = site)) },
            onConfirm = onNextForm,
            onGoHome = onGoHome
        )
        0 -> Form1LivraisonScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form1_livraison,
            onUpdate = onUpdate,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        1 -> Form2TemperatureScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form2_temperature,
            onUpdate = onUpdate,
            onPrevious = onPreviousForm,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        2 -> Form3SupportsScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form3_supports,
            onUpdate = onUpdate,
            onPrevious = onPreviousForm,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        3 -> Form4CollecteScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form4_collecte,
            onUpdate = onUpdate,
            onPrevious = onPreviousForm,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        4 -> FormSSCCScreen(
            delivery = delivery,
            onUpdate = onUpdate,
            onPrevious = onPreviousForm,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        5 -> Form5AnomaliesScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form5_anomalies,
            onUpdate = onUpdate,
            onAddAnomaly = onAddAnomaly,
            onUpdateAnomaly = onUpdateAnomaly,
            onRemoveAnomaly = onRemoveAnomaly,
            onPrevious = onPreviousForm,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        6 -> Form6RemarquesScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form6_remarks,
            onUpdate = onUpdate,
            onPrevious = onPreviousForm,
            onNext = onNextForm,
            onGoHome = onGoHome
        )
        7 -> Form7TerminerScreen(
            delivery = delivery,
            requiredFields = config.requiredFields.form7_finish,
            onUpdate = onUpdate,
            onPrevious = onPreviousForm,
            onFinish = onFinish,
            onGoHome = onGoHome
        )
    }
}
