package com.mytiki.publish.client.clo

import com.mytiki.apps_receipt_rewards.more.MoreContributor
import com.mytiki.publish.client.clo.merchant.MerchantEnum

class CloService {

    /**
     * Adds a card to the user's account.
     * @param last4 Last 4 digits of the card.
     * @param bin Bank Identification Number.
     * @param issuer Card issuer.
     * @param network Card network (VISA, MASTERCARD, AMERICAN EXPRESS, or DISCOVERY).
     */
    fun card(last4: String, bin: String, issuer: String, network: String){}

    /**
     * Retrieves card-linked offers for the user.
     * @return List of card-linked offers.
     */
    fun offers(merchant: MerchantEnum): List<Offer>{
        return listOf(
            Offer(
                bannerUrl = "https://m.media-amazon.com/images/W/MEDIAX_849526-T2/images/I/91PQ-g3YAHL._AC_SX695_.jpg",
                description = "3% off on shoes and socks",
                clickUrl = "https://www.amazon.com/s?k=shoes+and+socks&crid=31ZIZT1WD03E3&sprefix=shoes+and+so%2Caps%2C299&ref=nb_sb_ss_ts-doa-p_1_12",
                commissionType = ComissionEnum.PERCENT,
                totalCommission = 0.012
            ) ,
            Offer(
                bannerUrl = "https://m.media-amazon.com/images/W/MEDIAX_849526-T2/images/I/71vFKBpKakL.__AC_SY445_SX342_QL70_FMwebp_.jpg",
                description = "40% off on Macbook M1",
                clickUrl = "https://www.amazon.com/s?k=macbook&crid=1EUNQ6IZKWFQT&sprefix=mac%2Caps%2C333&ref=nb_sb_ss_ts-doa-p_2_3",
                commissionType = ComissionEnum.PERCENT,
                totalCommission = 0.5
            ),
            Offer(
                bannerUrl = "https://m.media-amazon.com/images/W/MEDIAX_849526-T2/images/I/71j8G9EH-DL.__AC_SX300_SY300_QL70_FMwebp_.jpg",
                description = "3% off on COSORI Air Fryer TurboBlaze",
                clickUrl = "https://www.amazon.com/s?k=air+fryer&crid=3OBRRISHQP34B&sprefix=air%2Caps%2C403&ref=nb_sb_ss_w_hit-vc-l_air-fryer_k2_3_3",
                commissionType = ComissionEnum.PERCENT,
                totalCommission = 0.012
            )
        )
    }

    /**
     * Retrieves information about the user's rewards.
     * @return List of user rewards.
     */
    fun rewards(): List<Reward>{
        return listOf()
    }

    /**
     * Sends transaction information to match card-linked offers.
     * @param transaction The transaction information.
     */
    fun transaction(transaction: Transaction){}

    /**
     * Retrieves a list of the largest contributors to the rewards program.
     *
     * @return A list of [MoreContributor] objects containing contributor details.
     */
    fun largestContributors(): List<MoreContributor> {
        return listOf(
            MoreContributor("Walmart", 0.4),
            MoreContributor("Kroger", 0.3),
            MoreContributor("Dollar General", 0.2)
        )
    }
}