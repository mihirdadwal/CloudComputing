build {
  name = var.build_name
  sources = [
    var.source_ami
  ]
  provisioner "shell" {
    script = var.update_script
  }
  provisioner "shell" {
    script = var.app_prereq_script
  }
  provisioner "file" {
    source      = var.jar_source
    destination = var.tmp_destination
  }
  provisioner "file" {
    source      = var.config_source
    destination = var.tmp_destination
  }
  provisioner "file" {
    source      = var.service_file_source
    destination = var.tmp_destination
  }
  provisioner "shell" {
    script = var.app_dir_setup_script
  }
}