package com.sahu.jethub.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.sahu.jethub.dataHolders.IssueItemDetails
import com.sahu.jethub.dataHolders.PRItemDetails
import com.sahu.jethub.ui.theme.JetHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchQueryData()

        setContent {
            JetHubTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Header(Modifier.fillMaxWidth())
                        DisplayItems()
                    }
                }
            }
        }
    }

    @Composable
    fun Header(modifier: Modifier = Modifier) {
        Box(modifier = modifier) {
            Button(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { viewModel.fetchQueryData() }
            ) {
                Text(text = "Fetch Data")
            }
        }
    }

    @Composable
    private fun DisplayItems() {
        val items by viewModel.data.collectAsState()

        LazyColumn {
            items(items) {
                when (it) {
                    is PRItemDetails -> PRItemDetailComposable(it)
                    is IssueItemDetails -> IssueItemDetailComposable(it)
                }
            }
        }
    }

    @Composable
    fun PRItemDetailComposable(itemDetails: PRItemDetails) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)) {
            Icon(getPrStatePainter(itemDetails), contentDescription = "PR State")
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = itemDetails.title, style = MaterialTheme.typography.body1)
        }
    }

    @Composable
    fun getPrStatePainter(pr: PRItemDetails): Painter =
        when {
            pr.state == "open" -> rememberVectorPainter(Icons.Default.Add)
            pr.isDraft -> rememberVectorPainter(Icons.Default.ArrowDropDown)
            pr.mergedAt.isNullOrBlank().not() -> rememberVectorPainter(Icons.Default.Favorite)
            pr.closedAt.isNullOrBlank().not() -> rememberVectorPainter(Icons.Default.Close)
            else -> rememberVectorPainter(Icons.Default.Add)
        }

    @Composable
    fun IssueItemDetailComposable(itemDetails: IssueItemDetails) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(getIssueStatePainter(itemDetails), contentDescription = "PR State")
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = itemDetails.title, style = MaterialTheme.typography.body1)
        }
    }

    @Composable
    fun getIssueStatePainter(issue: IssueItemDetails): Painter =
        when (issue.state) {
            "open" -> rememberVectorPainter(Icons.Default.Add)
            else -> rememberVectorPainter(Icons.Default.Close)
        }

}