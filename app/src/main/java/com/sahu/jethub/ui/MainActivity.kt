package com.sahu.jethub.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.sahu.jethub.dataHolders.IssueItemDetails
import com.sahu.jethub.dataHolders.ItemDetails
import com.sahu.jethub.dataHolders.PRItemDetails
import com.sahu.jethub.ui.theme.JetHubTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.fetchQueryData()

        setContent {
            JetHubTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    val items by viewModel.data.collectAsState()

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Header(Modifier.fillMaxWidth())
                        DisplayItems(
                            items,
                            viewModel::loadMoreQueryData,
                            viewModel.hasLoadMore.value
                        )
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
    private fun DisplayItems(listItems: List<ItemDetails>, loadMore: () -> Unit, isLoadMoreEnabled: Boolean = true) {

        val threshold = 3

        LazyColumn {
            itemsIndexed(listItems) { index, item ->

                if(isLoadMoreEnabled && index + threshold == listItems.size)
                    SideEffect { loadMore() }

                when (item) {
                    is PRItemDetails -> PRItemDetailComposable(item, index)
                    is IssueItemDetails -> IssueItemDetailComposable(item)
                }
            }

            if(isLoadMoreEnabled)
                item { LoadingIndicator() }

        }
    }

    @Composable
    fun PRItemDetailComposable(itemDetails: PRItemDetails, index: Int) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)) {
            Text(text = index.toString())
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

    @Composable
    fun LoadingIndicator() {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(50.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(Modifier.size(36.dp))
        }
    }
}