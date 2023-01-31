package app.cloud.local

import app.cloud.Cloud
import jakarta.inject.Singleton

@Singleton
class Local(override val name: String = "Local") : Cloud
