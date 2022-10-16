# Imports
from email.mime import image
import cv2
import imghdr
import ntpath
import numpy as np
import tkinter as tk
from tkinter import filedialog
from tkinter import font
from tkinter import messagebox
from tkinter import ttk


# Create an accumulator array based on the edges image
def constructAccumulatorArray(image):

    height, width = image.shape
    diagonal = np.sqrt(height * height + width * width)

    if diagonal != int(diagonal):   # convert to int (ceiling)
        diagonal = int(diagonal) + 1
    
    # construct the array, initial values are 0
    rhos = np.arange(-diagonal, diagonal, 1)
    thetas = np.deg2rad(np.arange(-90, 90, 1))
    accArray = np.zeros((len(rhos), len(thetas)), dtype = np.uint64)
    
    yIndices, xIndices = np.nonzero(image)  # get indices of edge points

    for i in range(len(xIndices)):
        x = xIndices[i]
        y = yIndices[i]

        # for each edge point, for each theta value calculate its rho value according to 
        # polar line equation, and increment the corresponding cell by 1
        for j in range(len(thetas)):
            rho = int((x * np.cos(thetas[j]) + y * np.sin(thetas[j]))) + diagonal   # value should be a non-negative integer
            accArray[rho, j] += 1

    return accArray, rhos, thetas


# Find peaks in the accumulator array, based on parameters passed by the user
def findMaxIndices(accArray, numOfLines):
    
    SQUARE_SIZE = 5 # can be defined according to desired accuracy and noise resilience
    indices = []

    # find the next peak, append it to peaks list and initialize nearby cells according to SQUARE_SIZE
    for i in range(numOfLines):
        flatIndex = np.argmax(accArray)
        tupleIndex = np.unravel_index(flatIndex, accArray.shape)
        indices.append(tupleIndex)
        yIndex, xIndex = tupleIndex

        xLeft = max(xIndex - SQUARE_SIZE, 0)
        xRight = min(xIndex + SQUARE_SIZE, accArray.shape[1])
        yBottom = max(yIndex - SQUARE_SIZE, 0)
        yTop = min(yIndex + SQUARE_SIZE, accArray.shape[0])

        for x in range(xLeft, xRight):
            for y in range(yBottom, yTop):
                accArray[y, x] = 0
    
    return indices


# Reverse engineer from the accumulator array values and draw corresponding lines across the entire image
def drawLines(thetas, rhos, indices, image):

    for i in range(len(indices)):
        rho = rhos[indices[i][0]]
        theta = thetas[indices[i][1]]

        # find two points on the line which exceed the image boundaries
        x1 = 0
        x2 = int(image.shape[1]) + 1

        if np.sin(theta) == 0:

            if (rho - x1 * np.cos(theta)) < 0:
                y1 = (rho - x1 * np.cos(theta)) / -0.00001
            else:
                y1 = (rho - x1 * np.cos(theta)) / 0.00001

            if (rho - x2 * np.cos(theta)) < 0:
                y2 = (rho - x2 * np.cos(theta)) / -0.00001
            else:
                y1 = (rho - x2 * np.cos(theta)) / 0.00001

        else:
            y1 = (rho - x1 * np.cos(theta)) / np.sin(theta)
            y2 = (rho - x2 * np.cos(theta)) / np.sin(theta)
        
        y1 = int(y1)
        y2 = int(y2)

        cv2.line(image, (x1, y1), (x2, y2), (0, 255, 0), 2)


# Show original image, then edges image (using Canny Edge Detection), then Hough Tranform image
def main(numOfLines, original, edges):

    root.destroy()

    # Original image
    inputImage = cv2.imread(imageName)

    if original == 1:
        cv2.imshow('Original', inputImage)
        cv2.waitKey(0)
        cv2.destroyAllWindows()

    # Edges image
    grayscaleImage = cv2.cvtColor(inputImage, cv2.COLOR_RGB2GRAY)
    blurredImage = cv2.GaussianBlur(grayscaleImage, (5, 5), 1.5)
    edgesImage = cv2.Canny(blurredImage, 100, 200)

    if edges == 1:
        cv2.imshow('Edges', edgesImage)
        cv2.waitKey(0)
        cv2.destroyAllWindows()

    # Hough image
    accArray, rhos, thetas = constructAccumulatorArray(edgesImage)
    indices = findMaxIndices(accArray, numOfLines)
    drawLines(thetas, rhos, indices, inputImage)
    cv2.imshow('Hough Tranform', inputImage)
    cv2.waitKey(0)
    cv2.destroyAllWindows()


# ============= #
# Construct GUI #
# ============= #

# Determine the state of Go button
def updateGoState(*args):

    if imageNameWrapper.get() != '' and numOfLines.get().isdigit() and int(numOfLines.get()) >= 1 and int(numOfLines.get()) <= 30:
        goButton.config(state = 'normal')

    else:
        goButton.config(state = 'disabled')

# Define the command of selectImageButton
def selectImage():

    global imageName
    prevImageName = ''

    if 'imageName' in globals():
        prevImageName = imageName

    imageName = filedialog.askopenfilename(title = 'Select an Image')

    if imageName != '' and not imghdr.what(imageName):
        imageName = prevImageName
        messagebox.showerror('File Format isn\'t Supported', 'The format of the file youv\'e selected is not supported.\nPlease select a different file.')
        return

    if imageName == '' and prevImageName != '':
        imageName = prevImageName
        return

    if imageName == '':
        return
    
    imageNameLabel = ttk.Label(frame2, text = 'Selected image is ' + ntpath.basename(imageName), width = 70, anchor = 'center', font = smallFont)
    imageNameLabel.grid(row = 2)

    # change imageNameWrapper value for trace to updateGoState function
    imageNameWrapper.set(imageName)

# Show warning if numOfLinesEntry is invalid 
def numOfLinesIsInvalid(*args):

    rangeLabel = ttk.Label(frame3, text = 'Value should be an integer between 1 and 30', font = smallFont, foreground = 'red', width = 40)

    if numOfLines.get() != '':

        if not numOfLines.get().isdigit():
            rangeLabel.grid(row = 2)

        elif int(numOfLines.get()) < 1 or int(numOfLines.get()) > 30:
            rangeLabel.grid(row = 2)

        else:
            emptyLabel2.tkraise()

    else:
        emptyLabel2.tkraise()

# Show message if 'Show original/edges image' options were chosen
def multipleImagesMessage(*args):

    if original.get() == 1 or edges.get() == 1:
        checkboxLabel.grid(row = 5, padx = 20, pady = 5, sticky = 'w')
        checkboxLabel.tkraise()

    else:
        emptyLabel3.tkraise()

# build main window
root = tk.Tk()
root.geometry('510x400')
root.resizable(False, False)
root.title('Hough Transform')

# define fonts
headerFont = font.Font(size = 30, weight = 'bold')
textFont = font.Font(size = 14)
smallFont = font.Font(size = 11)

frame1 = ttk.Frame(root)
frame1.grid(row = 0)

headerLabel = ttk.Label(frame1, text = 'Hough Tranform', font = headerFont)
headerLabel.grid(row = 0, pady = 5)

explanationLabel = ttk.Label(frame1, text = '   This program is designed to detect straight lines in an input image.', font = textFont)
explanationLabel.grid(row = 1, pady = 10)

frame2 = ttk.Frame(root)
frame2.grid(row = 1)

imageSelectionLabel = ttk.Label(frame2, text = 'Select a source image:', font = textFont)
imageSelectionLabel.grid(row = 0, pady = 8)

# trace imageNameWrapper for updateGoState function
imageNameWrapper = tk.StringVar(frame2)
imageNameWrapper.set('')
imageNameWrapper.trace('w', updateGoState)

selectImageButton = ttk.Button(frame2, text = 'Select an Image', command = selectImage)
selectImageButton.grid(row = 1)

emptyLabel1 = ttk.Label(frame2, width = 55)
emptyLabel1.grid(row = 2)

frame3 = ttk.Frame(root)
frame3.grid(row = 2)

numOfLinesLabel = ttk.Label(frame3, text = 'Enter number of lines to show:', font = textFont)
numOfLinesLabel.grid(row = 0, pady = 10)

numOfLines = tk.StringVar(frame3)
numOfLines.set('')
numOfLinesEntry = ttk.Entry(frame3, textvariable = numOfLines, width = 5)
numOfLinesEntry.grid(row = 1)

emptyLabel2 = ttk.Label(frame3, width = 40)
emptyLabel2.grid(row = 2)

# trace numOfLines for numOfLinesIsInvalid and updateGoState functions
numOfLines.trace('w', numOfLinesIsInvalid)
numOfLines.trace('w', updateGoState)

original = tk.IntVar()
edges = tk.IntVar()

# trace checkboxes values for multipleImagesMessage function
original.trace('w', multipleImagesMessage)
edges.trace('w', multipleImagesMessage)

originalCheckbox = ttk.Checkbutton(frame3, text = 'Show original image', variable = original, onvalue = 1, offvalue = 0)
edgesCheckbox = ttk.Checkbutton(frame3, text = 'Show edges image', variable = edges, onvalue = 1, offvalue = 0)
originalCheckbox.grid(row = 3, padx = 20, sticky = 'w')
edgesCheckbox.grid(row = 4, padx = 20, sticky = 'w')

checkboxLabel = ttk.Label(frame3, text = 'Hit Space to switch bewteen the images', font = smallFont)
emptyLabel3 = ttk.Label(frame3, width = 55)
emptyLabel3.grid(row = 5, pady = 5)

goButton = ttk.Button(root, text = 'Go!', command = lambda: main(int(numOfLines.get()), original.get(), edges.get()), state = 'disabled')
goButton.grid(row = 20, pady = 20)

root.mainloop()