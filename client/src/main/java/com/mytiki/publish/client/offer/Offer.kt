package com.mytiki.publish.client.offer

import com.mytiki.publish.client.permission.Permission

class Offer(
    val description: String,
    val rewards: List<Reward>,
    val use: Use,
    val tags: List<Tag>,
    val ptr: String,
    val permissions: List<Permission>? = null,
    val mutable: Boolean = true
)
