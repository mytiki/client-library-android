/*
 * Copyright (c) TIKI Inc.
 * MIT license. See LICENSE file in the root directory.
 */

package com.mytiki.publish.client.optIn

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.mytiki.publish.client.TikiClient
import com.mytiki.publish.client.databinding.ActivityOptInBinding
import com.mytiki.publish.client.optIn.navigation.ui.NavigationHost
import com.mytiki.publish.client.optIn.theme.OptInTheme

class OptInActivity : AppCompatActivity() {

  private var _binding: ActivityOptInBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    _binding = ActivityOptInBinding.inflate(layoutInflater)
    binding.composeView.apply {
      setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
      setContent {
        OptInTheme() {
          TikiClient.optIn.offer?.let {
            NavigationHost(this@OptInActivity, it) { this@OptInActivity.finish() }
          }
        }
      }
    }
    setContentView(binding.root)
  }
}
