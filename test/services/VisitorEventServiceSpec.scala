package services

import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers._

import scala.concurrent.Future

class VisitorEventServiceSpec extends PlaySpec with GuiceOneAppPerSuite with ScalaFutures with MockitoSugar {

  import models._

  import scala.concurrent.ExecutionContext.Implicits.global

  "VisitorEventService" should {

    "throw an exception for bad date" in {

      val vistorEventRepository: VistorEventRepository = mock[VistorEventRepository]

      when(vistorEventRepository.list(any(), any(), any(), any(), any(), any())).thenReturn(Future.successful(Seq.empty[VisitorEvent]))

      val visitorEventService = new VisitorEventService(vistorEventRepository)

      val thrown = intercept[RuntimeException] {
        visitorEventService.list(startDateStr = "rubbish date", boundaryListStr = "51.727327,-2.274689,51.678361,-2.176256", limit = None)
      }
      assert(thrown.getMessage === "missing parameters")

    }

    "throw an exception for missing or mangled boundary params" in {

      val vistorEventRepository: VistorEventRepository = mock[VistorEventRepository]

      when(vistorEventRepository.list(any(), any(), any(), any(), any(), any())).thenReturn(Future.successful(Seq.empty[VisitorEvent]))

      val visitorEventService = new VisitorEventService(vistorEventRepository)

      var thrown = intercept[RuntimeException] {
        visitorEventService.list(startDateStr = "2019-09-01T23:28:56.782Z", boundaryListStr = "51.727327,-2.274689", limit = None)
      }
      assert(thrown.getMessage === "missing parameters")

      thrown = intercept[RuntimeException] {
        visitorEventService.list(startDateStr = "2019-09-01T23:28:56.782Z", boundaryListStr = "parp", limit = None)
      }
      assert(thrown.getMessage === "missing parameters")

    }

    "call the vistorEventRepository when all parameters present " in {

      val vistorEventRepository: VistorEventRepository = mock[VistorEventRepository]

      when(vistorEventRepository.list(any(), any(), any(), any(), any(), any())).thenReturn(Future.successful(Seq.empty[VisitorEvent]))

      val visitorEventService = new VisitorEventService(vistorEventRepository)

      whenReady(visitorEventService.list(startDateStr = "2019-09-01T23:28:56.782Z", boundaryListStr = "51.727327,-2.274689,51.678361,-2.176256", limit = None)) { results =>
        results.size must equal(0)
      }

    }

  }

}
