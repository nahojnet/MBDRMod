package com.mbdrmod.delivery.data.model

import com.google.gson.annotations.SerializedName

data class WebhookPayload(
    @SerializedName("id")
    val id: Long,

    @SerializedName("timestamp")
    val timestamp: Long,

    @SerializedName("data")
    val data: DeliveryPayloadData
)

data class DeliveryPayloadData(
    @SerializedName("livraison")
    val livraison: LivraisonData,

    @SerializedName("temperature")
    val temperature: TemperatureData,

    @SerializedName("supports_livraison")
    val supportsLivraison: SupportsLivraisonData,

    @SerializedName("collecte")
    val collecte: CollecteData,

    @SerializedName("anomalies")
    val anomalies: List<AnomalyData>,

    @SerializedName("remarques")
    val remarques: String,

    @SerializedName("fin_livraison")
    val finLivraison: FinLivraisonData
)

data class LivraisonData(
    @SerializedName("client")
    val client: ClientData,

    @SerializedName("horaire")
    val horaire: HoraireData,

    @SerializedName("quantite")
    val quantite: QuantiteData
)

data class ClientData(
    @SerializedName("date_livraison")
    val dateLivraison: String,

    @SerializedName("numero_client")
    val numeroClient: String,

    @SerializedName("numero_tour")
    val numeroTour: Int?
)

data class HoraireData(
    @SerializedName("heure_prevue_arrivee")
    val heurePrevueArrivee: String,

    @SerializedName("heure_reelle_arrivee")
    val heureReelleArrivee: String,

    @SerializedName("heure_debut_livraison")
    val heureDebutLivraison: String
)

data class QuantiteData(
    @SerializedName("supports")
    val supports: Int?,

    @SerializedName("poids_kg")
    val poidsKg: Double?,

    @SerializedName("colis")
    val colis: Int?,

    @SerializedName("volume_m3")
    val volumeM3: Double?
)

data class TemperatureData(
    @SerializedName("vehicule")
    val vehicule: VehiculeTemperatureData,

    @SerializedName("produits")
    val produits: ProduitsTemperatureData
)

data class VehiculeTemperatureData(
    @SerializedName("surgele")
    val surgele: Double?,

    @SerializedName("frais")
    val frais: Double?
)

data class ProduitsTemperatureData(
    @SerializedName("surgele")
    val surgele: ProduitTemperatureDetail,

    @SerializedName("frais")
    val frais: ProduitTemperatureDetail
)

data class ProduitTemperatureDetail(
    @SerializedName("temperature")
    val temperature: Double?,

    @SerializedName("methode")
    val methode: String
)

data class SupportsLivraisonData(
    @SerializedName("dolies")
    val dolies: SupportDetail,

    @SerializedName("demi_pal")
    val demiPal: SupportDetail,

    @SerializedName("europe")
    val europe: SupportDetail,

    @SerializedName("autres")
    val autres: SupportDetail,

    @SerializedName("panieres")
    val panieres: SupportDetail,

    @SerializedName("megabib")
    val megabib: SupportDetail
)

data class SupportDetail(
    @SerializedName("livrees")
    val livrees: Int?,

    @SerializedName("rendues")
    val rendues: Int?
)

data class CollecteData(
    @SerializedName("films_plastiques")
    val filmsPlastiques: Int?,

    @SerializedName("cartons_collecte")
    val cartonsCollecte: Int?,

    @SerializedName("aluminium")
    val aluminium: Int?
)

data class AnomalyData(
    @SerializedName("wrin")
    val wrin: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("quantite")
    val quantite: Int?,

    @SerializedName("manquant")
    val manquant: Int?,

    @SerializedName("refus")
    val refus: Int?,

    @SerializedName("excedent")
    val excedent: Int?,

    @SerializedName("motif")
    val motif: String
)

data class FinLivraisonData(
    @SerializedName("heure_fin_livraison")
    val heureFinLivraison: String,

    @SerializedName("heure_depart")
    val heureDepart: String,

    @SerializedName("nom_conducteur")
    val nomConducteur: String,

    @SerializedName("nom_manager")
    val nomManager: String
)

// Extension function to convert DeliveryData to WebhookPayload
fun DeliveryData.toWebhookPayload(): WebhookPayload {
    return WebhookPayload(
        id = this.id,
        timestamp = System.currentTimeMillis(),
        data = DeliveryPayloadData(
            livraison = LivraisonData(
                client = ClientData(
                    dateLivraison = this.deliveryDate,
                    numeroClient = this.clientNumber,
                    numeroTour = this.tourNumber
                ),
                horaire = HoraireData(
                    heurePrevueArrivee = this.expectedArrivalTime,
                    heureReelleArrivee = this.actualArrivalTime,
                    heureDebutLivraison = this.deliveryStartTime
                ),
                quantite = QuantiteData(
                    supports = this.supports,
                    poidsKg = this.weightKg,
                    colis = this.packages,
                    volumeM3 = this.volumeM3
                )
            ),
            temperature = TemperatureData(
                vehicule = VehiculeTemperatureData(
                    surgele = this.vehicleFrozenTemp,
                    frais = this.vehicleFreshTemp
                ),
                produits = ProduitsTemperatureData(
                    surgele = ProduitTemperatureDetail(
                        temperature = this.productFrozenTemp,
                        methode = this.productFrozenMethod
                    ),
                    frais = ProduitTemperatureDetail(
                        temperature = this.productFreshTemp,
                        methode = this.productFreshMethod
                    )
                )
            ),
            supportsLivraison = SupportsLivraisonData(
                dolies = SupportDetail(
                    livrees = this.doliesDelivered,
                    rendues = this.doliesReturned
                ),
                demiPal = SupportDetail(
                    livrees = this.halfPalDelivered,
                    rendues = this.halfPalReturned
                ),
                europe = SupportDetail(
                    livrees = this.europeDelivered,
                    rendues = this.europeReturned
                ),
                autres = SupportDetail(
                    livrees = this.othersDelivered,
                    rendues = this.othersReturned
                ),
                panieres = SupportDetail(
                    livrees = this.panieresDelivered,
                    rendues = this.panieresReturned
                ),
                megabib = SupportDetail(
                    livrees = this.megabibDelivered,
                    rendues = this.megabibReturned
                )
            ),
            collecte = CollecteData(
                filmsPlastiques = this.plasticFilms,
                cartonsCollecte = this.cardboard,
                aluminium = this.aluminum
            ),
            anomalies = this.anomalies.map { anomaly ->
                AnomalyData(
                    wrin = anomaly.wrin,
                    description = anomaly.description,
                    quantite = anomaly.quantity,
                    manquant = anomaly.missing,
                    refus = anomaly.refused,
                    excedent = anomaly.excess,
                    motif = anomaly.reason
                )
            },
            remarques = this.remarks,
            finLivraison = FinLivraisonData(
                heureFinLivraison = this.deliveryEndTime,
                heureDepart = this.departureTime,
                nomConducteur = this.driverName,
                nomManager = this.managerName
            )
        )
    )
}
