/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in root directory.
 */

package com.mytiki.publish.client.clo.merchant

import com.mytiki.publish.client.ProvidersInterface
import com.mytiki.publish.client.R
import com.mytiki.publish.client.email.EmailProviderEnum


/**
 * [RetailerEnum] enum represents various retailer names as raw string values.
 */
enum class MerchantEnum: ProvidersInterface {
    ACME_MARKETS,
    ALBERTSONS,
    AMAZON,
    BED_BATH_AND_BEYOND,
    BESTBUY,
    BJS_WHOLESALE,
    CHEWY,
    COSTCO,
    CVS,
    DICKS_SPORTING_GOODS,
    DOLLAR_GENERAL,
    DOLLAR_TREE,
    DOMINOS_PIZZA,
    DOOR_DASH,
    DRIZLY,
    FAMILY_DOLLAR,
    FOOD_4_LESS,
    FOOD_LION,
    FRED_MEYER,
    GAP,
    GIANT_EAGLE,
    GRUBHUB,
    HARRIS_TEETER,
    HEB,
    HOME_DEPOT,
    HYVEE,
    INSTACART,
    JEWEL_OSCO,
    KOHLS,
    KROGER,
    LOWES,
    MACYS,
    MARSHALLS,
    MEIJER,
    NIKE,
    PUBLIX,
    RALPHS,
    RITE_AID,
    SAFEWAY,
    SAMS_CLUB,
    SEAMLESS,
    SEPHORA,
    SHIPT,
    SHOPRITE,
    SPROUTS,
    STAPLES,
    STARBUCKS,
    TACO_BELL,
    TARGET,
    TJ_MAXX,
    UBER_EATS,
    ULTA,
    VONS,
    WALGREENS,
    WALMART,
    WEGMANS;

    companion object {
        fun fromString(name: String) = EmailProviderEnum.entries.first{name == it.toString()}
    }

    override fun toString() = this.name


    override fun resId(): Int {
        when (this) {
            ACME_MARKETS -> return R.drawable.acme
            ALBERTSONS -> return R.drawable.albertsons
            AMAZON -> return R.drawable.amazon
            BED_BATH_AND_BEYOND -> return R.drawable.bed_bath_beyond
            BESTBUY -> return R.drawable.best_buy
            BJS_WHOLESALE -> return R.drawable.bjs
            CHEWY -> return R.drawable.chewy
            COSTCO -> return R.drawable.costco
            CVS -> return R.drawable.cvs
            DICKS_SPORTING_GOODS -> return R.drawable.dicks
            DOLLAR_GENERAL -> return R.drawable.dollar_general
            DOLLAR_TREE -> return R.drawable.dollar_tree
            DOMINOS_PIZZA -> return R.drawable.dominos
            DOOR_DASH -> return R.drawable.door_dash
            DRIZLY -> return R.drawable.drizly
            FAMILY_DOLLAR -> return R.drawable.family_dollar
            FOOD_4_LESS -> return R.drawable.food_4_less
            FOOD_LION -> return R.drawable.food_lion
            FRED_MEYER -> return R.drawable.fred_meyer
            GAP -> return R.drawable.gap
            GIANT_EAGLE -> return R.drawable.giant_eagle
            GRUBHUB -> return R.drawable.grubhub
            HARRIS_TEETER -> return R.drawable.harris_teeter
            HEB -> return R.drawable.heb
            HOME_DEPOT -> return R.drawable.home_depot
            HYVEE -> return R.drawable.hy_vee
            INSTACART -> return R.drawable.instacart
            JEWEL_OSCO -> return R.drawable.jewel_osco
            KOHLS -> return R.drawable.kohls
            KROGER -> return R.drawable.kroger
            LOWES -> return R.drawable.lowes
            MACYS -> return R.drawable.macys
            MARSHALLS -> return R.drawable.marshalls
            MEIJER -> return R.drawable.meijer
            NIKE -> return R.drawable.nike
            PUBLIX -> return R.drawable.publix
            RALPHS -> return R.drawable.ralphs
            RITE_AID -> return R.drawable.rite_aid
            SAFEWAY -> return R.drawable.safeway
            SAMS_CLUB -> return R.drawable.sams_club
            SEAMLESS -> return R.drawable.seamless
            SEPHORA -> return R.drawable.sephora
            SHIPT -> return R.drawable.shipt
            SHOPRITE -> return R.drawable.shop_rite
            SPROUTS -> return R.drawable.sprouts
            STAPLES -> return R.drawable.staples
            STARBUCKS -> return R.drawable.starbucks
            TACO_BELL -> return R.drawable.taco_bell
            TARGET -> return R.drawable.target
            TJ_MAXX -> return R.drawable.tj_maxx
            UBER_EATS -> return R.drawable.uber_eats
            ULTA -> return R.drawable.ulta
            VONS -> return R.drawable.vons
            WALGREENS -> return R.drawable.walgreens
            WALMART -> return R.drawable.walmart
            WEGMANS -> return R.drawable.wegmans
        }
    }
}
