/*
 * Copyright (c) 2024 Indra Azimi. All rights reserved.
 *
 * Dibuat untuk buku berjudul "Pemrograman Android Lanjut".
 * Dilarang melakukan penggandaan dan atau komersialisasi,
 * sebagian atau seluruh bagian, baik cetak maupun elektronik
 * terhadap project ini tanpa izin pemilik hak cipta.
 */

package com.indraazimi.mobpro2s.ui

import android.content.Intent
import android.graphics.drawable.AdaptiveIconDrawable
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.indraazimi.mobpro2s.R

@Composable
fun WelcomeScreen(
    @DrawableRes appLogo: Int,
    @StringRes appName: Int,
    modifier: Modifier = Modifier
) {
    val contract = FirebaseAuthUIActivityResultContract()
    val launcher = rememberLauncherForActivityResult(contract) {}

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .padding(bottom = 32.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            painter = adaptiveIconPainterResource(appLogo),
            contentDescription = stringResource(appName)
        )
        Text(
            text = stringResource(appName),
            fontSize = 32.sp
        )
        Text(
            text = stringResource(R.string.login_intro),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp, 72.dp, 16.dp, 16.dp)
        )
        Button(onClick = { launcher.launch(getSigninIntent()) }) {
            Text(text = stringResource(R.string.login))
        }
    }
}

@Composable
private fun adaptiveIconPainterResource(@DrawableRes id: Int): Painter {
    val res = LocalContext.current.resources
    val theme = LocalContext.current.theme

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val adaptiveIcon = ResourcesCompat.getDrawable(res, id, theme) as? AdaptiveIconDrawable
        if (adaptiveIcon != null) {
            BitmapPainter(adaptiveIcon.toBitmap().asImageBitmap())
        } else {
            painterResource(id)
        }
    } else {
        painterResource(id)
    }
}

private fun getSigninIntent(): Intent {
    return AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAvailableProviders(
            arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        )
        .build()
}