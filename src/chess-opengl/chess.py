from OpenGL.GL import *
from OpenGL.GLU import *
from OpenGL.GLUT import *
import math
import sys

camera_angle = 0.0
camera_radius = 8.0
camera_height = 6.0
animation_speed = 0.1

pawns = [
    (0, 0, True), (0, 1, True), (0, 2, True), (0, 3, True),
    (0, 4, True), (0, 5, True), (0, 6, True), (0, 7, True),

    (7, 0, False), (7, 1, False), (7, 2, False), (7, 3, False),
    (7, 4, False), (7, 5, False), (7, 6, False), (7, 7, False)
]


def init():
    glEnable(GL_DEPTH_TEST)
    glEnable(GL_LIGHTING)
    glEnable(GL_NORMALIZE)
    glClearColor(0.2, 0.2, 0.4, 1.0)

    mat_ambient = [0.2, 0.2, 0.2, 1.0]
    mat_diffuse = [0.8, 0.8, 0.8, 1.0]
    mat_specular = [1.0, 1.0, 1.0, 1.0]
    mat_shininess = [100.0]

    glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient)
    glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse)
    glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular)
    glMaterialfv(GL_FRONT, GL_SHININESS, mat_shininess)

    ambient_light = [0.2, 0.2, 0.2, 1.0]
    glLightModelfv(GL_LIGHT_MODEL_AMBIENT, ambient_light)


def setup_lights():
    glEnable(GL_LIGHT0)
    light0_position = [-2.0, 8.0, -2.0, 1.0]
    light0_direction = [0.0, -1.0, 0.0]
    light0_diffuse = [0.8, 0.7, 0.6, 1.0]
    light0_specular = [1.0, 0.9, 0.8, 1.0]

    glLightfv(GL_LIGHT0, GL_POSITION, light0_position)
    glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light0_direction)
    glLightfv(GL_LIGHT0, GL_DIFFUSE, light0_diffuse)
    glLightfv(GL_LIGHT0, GL_SPECULAR, light0_specular)
    glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 45.0)
    glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 2.0)


    glEnable(GL_LIGHT1)
    light1_position = [6.0, 8.0, 6.0, 1.0]
    light1_direction = [0.0, -1.0, 0.0]
    light1_diffuse = [0.6, 0.6, 0.8, 1.0]
    light1_specular = [0.8, 0.8, 1.0, 1.0]

    glLightfv(GL_LIGHT1, GL_POSITION, light1_position)
    glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, light1_direction)
    glLightfv(GL_LIGHT1, GL_DIFFUSE, light1_diffuse)
    glLightfv(GL_LIGHT1, GL_SPECULAR, light1_specular)
    glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 45.0)
    glLightf(GL_LIGHT1, GL_SPOT_EXPONENT, 2.0)


    glEnable(GL_LIGHT2)
    light2_position = [4.0, 1.5, 4.0, 1.0]
    light2_diffuse = [0.0, 0.0, 0.5, 1.0]
    light2_specular = [0.1, 0.1, 0.4, 1.0]

    glLightfv(GL_LIGHT2, GL_POSITION, light2_position)
    glLightfv(GL_LIGHT2, GL_DIFFUSE, light2_diffuse)
    glLightfv(GL_LIGHT2, GL_SPECULAR, light2_specular)


def draw_chess_square(x, z, is_white):
    glPushMatrix()
    glTranslatef(x, 0.0, z)
    glScalef(1.0, 0.1, 1.0)

    white_diffuse = [0.9, 0.9, 0.8, 1.0]
    black_diffuse = [0.2, 0.1, 0.1, 1.0]

    if is_white:
        glMaterialfv(GL_FRONT, GL_DIFFUSE, white_diffuse)
    else:
        glMaterialfv(GL_FRONT, GL_DIFFUSE, black_diffuse)

    glutSolidCube(1.0)
    glPopMatrix()


def draw_chessboard():
    for row in range(8):
        for col in range(8):
            is_white = (row + col) % 2 == 0
            draw_chess_square(col, row, is_white)


def draw_pawn(is_white):
    white_diffuse = [0.95, 0.95, 0.9, 1.0]
    white_ambient = [0.9, 0.9, 0.85, 1.0]
    black_diffuse = [0.15, 0.15, 0.2, 1.0]
    black_ambient = [0.1, 0.1, 0.15, 1.0]

    if is_white:
        glMaterialfv(GL_FRONT, GL_DIFFUSE, white_diffuse)
        glMaterialfv(GL_FRONT, GL_AMBIENT, white_ambient)
    else:
        glMaterialfv(GL_FRONT, GL_DIFFUSE, black_diffuse)
        glMaterialfv(GL_FRONT, GL_AMBIENT, black_ambient)

    glPushMatrix()
    glTranslatef(0.0, 0.05, 0.0)
    glScalef(0.3, 0.05, 0.3)
    glutSolidSphere(1.0, 20, 20)
    glPopMatrix()

    glPushMatrix()
    glTranslatef(0.0, 0.10, 0.0)
    glScalef(0.23, 0.1, 0.23)
    glutSolidSphere(1.0, 20, 20)
    glPopMatrix()

    glPushMatrix()
    glTranslatef(0.0, 0.25, 0.0)
    glScalef(0.1, 0.35, 0.1)
    glutSolidSphere(1.0, 20, 20)
    glPopMatrix()

    glPushMatrix()
    glTranslatef(0.0, 0.4, 0.0)
    glScalef(0.17, 0.05, 0.17)
    glutSolidSphere(1.0, 20, 20)
    glPopMatrix()

    glPushMatrix()
    glTranslatef(0.0, 0.6, 0.0)
    glScalef(0.17, 0.17, 0.17)
    glutSolidSphere(1.0, 20, 20)
    glPopMatrix()


def draw_all_pawns():
    for row, col, is_white in pawns:
        glPushMatrix()
        glTranslatef(col, 0.05, row)
        draw_pawn(is_white)
        glPopMatrix()


def display():
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()

    cam_x = 3.5 + camera_radius * math.cos(camera_angle)
    cam_z = 3.5 + camera_radius * math.sin(camera_angle)
    gluLookAt(cam_x, camera_height, cam_z,
              3.5, 0.0, 3.5,
              0.0, 1.0, 0.0)

    setup_lights()
    draw_chessboard()
    draw_all_pawns()

    glutSwapBuffers()


def reshape(width, height):
    if height == 0:
        height = 1

    glViewport(0, 0, width, height)

    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluPerspective(45.0, float(width) / float(height), 0.1, 100.0)

    glMatrixMode(GL_MODELVIEW)


def idle():
    global camera_angle
    camera_angle += animation_speed * 0.01
    if camera_angle > 2 * math.pi:
        camera_angle -= 2 * math.pi
    glutPostRedisplay()

def main():
    glutInit(sys.argv)
    glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH)
    glutInitWindowSize(1200, 800)
    glutInitWindowPosition(100, 100)
    glutCreateWindow(b"Chess OpenGL")

    init()

    glutDisplayFunc(display)
    glutReshapeFunc(reshape)
    glutIdleFunc(idle)

    glutMainLoop()


if __name__ == "__main__":
    main()