package jp.toastkid.yobidashi4.infrastructure.service.web.screenshot

import java.awt.Component
import java.awt.Point
import java.awt.Robot
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID
import javax.imageio.ImageIO
import javax.swing.SwingUtilities

class ScreenshotExporter {

    operator fun invoke(uiComponent: Component) {
        val folder = Path.of("user/screenshot")
        if (Files.exists(folder).not()) {
            Files.createDirectories(folder)
        }
        val outputStream = Files.newOutputStream(folder.resolve("${UUID.randomUUID()}.png"))
        outputStream.use {
            val p = Point(0, 0)
            SwingUtilities.convertPointToScreen(p, uiComponent)
            val region = uiComponent.bounds
            region.x = p.x
            region.y = p.y
            if (region.x == 0 || region.y == 0) {
                return
            }

            val screenshot = Robot().createScreenCapture(region)
            ImageIO.write(screenshot, "png", it)
        }
    }

}