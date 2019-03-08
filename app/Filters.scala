import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter

class Filters (corsFilter: CORSFilter) extends HttpFilters {
  def filters = Seq(corsFilter)
}