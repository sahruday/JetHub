package com.sahu.jethub.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.sahu.jethub.R
import com.sahu.jethub.dataHolders.IssueItemDetails
import com.sahu.jethub.dataHolders.ItemDetails
import com.sahu.jethub.dataHolders.PRItemDetails
import com.sahu.jethub.ui.theme.Gray
import com.sahu.jethub.ui.theme.Green
import com.sahu.jethub.ui.theme.JetHubTheme
import com.sahu.jethub.ui.theme.Purple
import com.sahu.jethub.ui.theme.Red
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

                    val scaffoldState = rememberScaffoldState()

                    Scaffold(
                        topBar = { Header() },
                        scaffoldState = scaffoldState,
                    ) {
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
        var repo by remember { mutableStateOf("") }
        var query by remember { mutableStateOf(viewModel.query.value) }

        Column(modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)) {
            OutlinedTextField(
                value = repo,
                onValueChange = { repo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = MainViewModel.DEFAULT_REPO) },
                label = { Text(text = "Repository") },
                trailingIcon = {
                    Button(
                        onClick = {
                            val repository = if (repo.isNotBlank()) repo else MainViewModel.DEFAULT_REPO
                            val filterQuery = if (query.isNotBlank()) query else MainViewModel.DEFAULT_QUERY
                            if(repository != viewModel.repo.value || filterQuery != viewModel.query.value) {
                                viewModel.repo.value = repository; viewModel.query.value = filterQuery
                                viewModel.fetchQueryData()
                            }
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(text = "Fetch")
                    }
                }
            )

            BasicTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 4.dp, end = 4.dp),
            )
            Divider(thickness = 1.dp, color = MaterialTheme.colors.primary)
        }
    }

    @Composable
    private fun DisplayItems(listItems: List<ItemDetails>, loadMore: () -> Unit, isLoadMoreEnabled: Boolean = true) {

        val threshold = 3

        LazyColumn {
            itemsIndexed(listItems) { index, item ->
                if (isLoadMoreEnabled && index + threshold == listItems.size)
                    SideEffect { loadMore() }

                when (item) {
                    is PRItemDetails -> PRItemDetailComposable(item, index)
                    is IssueItemDetails -> IssueItemDetailComposable(item)
                }
            }

            item { LoadingIndicator(isLoadMoreEnabled) }
        }
    }

    @Composable
    fun PRItemDetailComposable(
        itemDetails: PRItemDetails,
        index: Int,
        modifier: Modifier = Modifier,
    ) {
        Column(modifier = modifier) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 8.dp)) {
//            Text(text = index.toString())
                Icon(getPrStatePainter(itemDetails), contentDescription = "PR State", tint = getPRImageColor(itemDetails),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${viewModel.repo.value} #${itemDetails.number}",
                            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Light),
                            modifier = Modifier.padding(horizontal = 0.dp, vertical = 2.dp),
                        )
                        Box(modifier = Modifier.weight(1f))
                        DateDisplay(itemDetails.createdAt)
                    }
                    Text(text = itemDetails.title,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(vertical = 2.dp))
                    itemDetails.user?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(painter = rememberImagePainter(data = it.avatarUrl), contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = it.name, style = MaterialTheme.typography.caption)
                        }
                    }
                }
            }
            Divider(thickness = 1.dp)
        }
    }

    @Composable
    fun DateDisplay(
        date: String,
        modifier: Modifier = Modifier,
    ) {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(R.drawable.ic_clock), contentDescription = "Clock", modifier = Modifier.size(16.dp))
            Text(text = date, modifier = Modifier.padding(horizontal = 4.dp), style = MaterialTheme.typography.caption)
        }
    }

    @Composable
    fun getPrStatePainter(pr: PRItemDetails): Painter =
        when {
            pr.state == "open" -> painterResource(R.drawable.ic_open_pr)
            pr.mergedAt.isNullOrBlank().not() -> rememberVectorPainter(Icons.Default.Favorite)
            else -> rememberVectorPainter(Icons.Default.Close)
        }

    @Composable
    fun getPRImageColor(pr: PRItemDetails): Color =
        when {
            pr.isDraft -> Gray
            pr.state == "open" -> Green
            pr.mergedAt.isNullOrBlank().not() -> Purple
            else -> Red
        }

    @Composable
    fun IssueItemDetailComposable(
        itemDetails: IssueItemDetails,
        modifier: Modifier = Modifier,
    ) {
        Row(modifier = modifier.fillMaxWidth()) {
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
    fun LoadingIndicator(
        showLoadingIndicator: Boolean,
        modifier: Modifier = Modifier,
    ) {
        Box(modifier = modifier
            .fillMaxWidth()
            .height(150.dp), contentAlignment = Alignment.Center) {
            if (showLoadingIndicator)
                CircularProgressIndicator(Modifier
                    .size(36.dp)
                    .padding(top = 14.dp))
        }
    }
}